package com.edu.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "student")
public class Student {
    @Id
    @GenericGenerator(name = "student-id-sequence",
            strategy = "com.edu.domain.generator.StudentIdentifierGenerator",
            parameters = {@Parameter(name = "sequence_prefix", value = "M"),
                          @Parameter(name = "max_digits", value = "5")})
    @GeneratedValue(generator = "student-id-sequence",
            strategy = GenerationType.TABLE)
    protected String id;

    private String studentName;

    @Column(updatable = false)
    private String birthday;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    @JsonIgnore
    private Customer customer;

    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Image> imagesSet;

    @ManyToMany(cascade = {CascadeType.MERGE},
            fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinTable(name = "STUDENT_COURSE",
            joinColumns = @JoinColumn(name = "STUDENT_ID",
                    referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "COURSE_ID",
                    referencedColumnName = "ID"))
    private Set<Course> coursesSet;

    @ManyToMany(cascade = {CascadeType.MERGE},
            fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinTable(name = "RESERVED_STUDENT_RESERVED_COURSE",
            joinColumns = @JoinColumn(name = "RESERVED_STUDENT_ID",
                    referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "RESERVED_COURSE_ID",
                    referencedColumnName = "ID"))
    private Set<Course> reservedCoursesSet;

    @ManyToMany(cascade = {CascadeType.MERGE},
            fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinTable(name = "STUDENT_NO_SIGN_COURSE",
            joinColumns = @JoinColumn(name = "STUDENT_ID",
                    referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "NO_SIGN_COURSE_ID",
                    referencedColumnName = "ID"))
    private Set<Course> courseNotSignSet;
    
    @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "student")
    @JsonIgnore
    private Set<CourseProduct> courseProducts = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name="STUDENT_COURSECATEGORY", joinColumns=@JoinColumn(name="STUDENT_ID", referencedColumnName="ID"))
	@MapKeyJoinColumn(name="COURSECATEGORY_ID", referencedColumnName="ID")
	@Column(name="COUNT_IN_COURSECATEGORY")
    private Map<CourseCategory, Integer> courseCount = new HashMap<>();

    private boolean isChild;
    
    private Gender gender;

    public static enum Gender {
    	BOY,
    	GIRL
	}
    
    public Student() {
    }

    public Student(String studentName, String birthday, Gender gender) {
        this.studentName = studentName;
        this.birthday = birthday;
        this.gender = gender;
        this.isChild = true;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setChild(boolean isChild) {
        this.isChild = isChild;
    }

    public void addImage(Image image) {
        this.imagesSet.add(image);
    }
    
    public void removeImage(Image image) {
        this.imagesSet.remove(image);
    }

    public Set<Course> getCoursesSet() {
        return coursesSet;
    }

    public void setCoursesSet(Set<Course> coursesSet) {
        this.coursesSet = coursesSet;
    }

    public void addCourse(Course course) {
        this.coursesSet.add(course);
    }

    public Set<Image> getImagesSet() {
        return imagesSet;
    }

    public void setImagesSet(Set<Image> imagesSet) {
        this.imagesSet = imagesSet;
    }

    public Set<Course> getReservedCoursesSet() {
        return reservedCoursesSet;
    }

    public void setReservedCoursesSet(Set<Course> reservedCoursesSet) {
        this.reservedCoursesSet = reservedCoursesSet;
    }

    public void addReservedCourse(Course reservedCourse) {
        this.reservedCoursesSet.add(reservedCourse);
    }
    
    public void removeReservedCourse(Course reservedCourse) {
        this.reservedCoursesSet.remove(reservedCourse);
    }


    public Set<Course> getCourseNotSignSet() {
        return courseNotSignSet;
    }

    public void setCourseNotSignSet(Set<Course> courseNotSignSet) {
        this.courseNotSignSet = courseNotSignSet;
    }

    public void addCourseNotSign(Course courseNotSign) {
        this.courseNotSignSet.add(courseNotSign);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Set<CourseProduct> getCourseProducts() {
		return courseProducts;
	}

	public void setCourseProducts(Set<CourseProduct> courseProducts) {
		this.courseProducts = courseProducts;
	}

	public Map<CourseCategory, Integer> getCourseCount() {
		return courseCount;
	}

	public void addCourseCount(CourseCategory courseCategory) {
		Integer count = this.courseCount.get(courseCategory);
		if(count == null){
			this.courseCount.put(courseCategory, courseCategory.getPeriod());
		}else{
			this.courseCount.put(courseCategory, count+courseCategory.getPeriod());
		}
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", studentName=" + studentName + ", birthday=" + birthday + ", customer="
				+ customer + ", imagesSet=" + imagesSet + ", coursesSet=" + coursesSet + ", reservedCoursesSet="
				+ reservedCoursesSet + ", courseNotSignSet=" + courseNotSignSet + ", courseProducts=" + courseProducts
				+ ", courseCount=" + courseCount + ", isChild=" + isChild + ", gender=" + gender + "]";
	}
	
}
