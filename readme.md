# How to Deploy

## Development Server

1. Use `ssh` to logon to server
2. Execute the following command
    ```sh
    $ tmux list-sessions
    ```
    This should give out the following output
    ```sh
    $ tmux list-sessions
    0: 1 windows (created Fri Dec  1 22:27:10 2017) [204x55]
    ```
    
    Once you see the output above, run command
    ```sh
    $ tmux attach
    ```

    Or if you don't see any existing sessions, create a new one via 
    ```sh 
    $ tmux
    ```
3. If the service is running, hit `Ctrl + C` to shut it down
4. (Optional) Reset database content by the command below
    ```sh
    $ mycli -h localhost -u <dbuser>
    ```
    Enter the password when prompted. Then
    
    ```
    <dbuser>@localhost:(none)> drop database club;
    You're about to run a destructive command.
    Do you want to proceed? (y/n): y
    Your call!
    Query OK.
    Time: 0.420s
    <dbuser>@localhost:(none)> CREATE DATABASE `club` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
    Query OK, 1 row affected
    Time: 0.001s
    <dbuser>@localhost:(none)> quit;
    Goodbye!
    ```
5. Under the root directory of the project, issue command
    ```sh
    $ ./pull-build-and-run.sh 
    ```
6. Then the service is updated to the most recent commit in master branch and is up-and-running again