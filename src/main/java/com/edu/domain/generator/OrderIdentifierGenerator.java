package com.edu.domain.generator;

import com.edu.domain.Identifiable;
import org.hibernate.MappingException;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class OrderIdentifierGenerator extends AbstractIdentifierGenerator
        implements Configurable {

    public static final String MAX_DIGITS = "max_digits";
    private static final String ORDER_SEQUENCE_NAME = "order_seq";

    private int maxDigits;

    private static final Logger logger = LoggerFactory.getLogger(OrderIdentifierGenerator.class);

    @Override
    public void configure(
            Type type, Properties params, ServiceRegistry serviceRegistry)
            throws MappingException {

        final ConfigurationService configurationService =
                serviceRegistry.getService(ConfigurationService.class);
        int globalEntityIdentifierMaxLength =
                configurationService.getSetting("entity.identifier.max-digits", Integer.class, 8);

        maxDigits = ConfigurationHelper.getInt(
                MAX_DIGITS,
                params,
                globalEntityIdentifierMaxLength);
    }

    @Override
    public Serializable generate(SessionImplementor session, Object obj) {
        if (obj instanceof Identifiable) {
            @SuppressWarnings("rawtypes")
            Identifiable identifiable = (Identifiable) obj;
            Serializable id = identifiable.getId();
            if (id != null) {
                return id;
            }
        }

        String generatedId = null;

        Connection connection = session.connection();

        try {
            PreparedStatement ps1 = connection.prepareStatement("SELECT tbl.next_val FROM sequence tbl WHERE tbl.seq_name=? FOR UPDATE");
            ps1.setString(1, ORDER_SEQUENCE_NAME);

            ResultSet rs = ps1.executeQuery();

            if(rs.next())
            {
                Long seqValue = rs.getLong(1);
                if (digitsOf(seqValue) > maxDigits) {
                    throw new SequenceNumberUsedUpException("No more free order id! Please contact support engineer");
                }
                String format = "%0" + maxDigits + "d"; // e.g. %05d
                String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // e.g. '20171201'
                generatedId = today + String.format(format, seqValue); // padding leading zero

                logger.debug("Generated Id: " + generatedId);

                Long nextValue = generateNextValue(seqValue);

                PreparedStatement ps2 = connection.prepareStatement("UPDATE sequence SET next_val=? WHERE next_val=? AND seq_name=?");
                ps2.setLong(1, nextValue);
                ps2.setLong(2, seqValue);
                ps2.setString(3, ORDER_SEQUENCE_NAME);
                ps2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return generatedId;
    }

    private Long generateNextValue(Long currentValue) {
        return currentValue + 1;
    }
}