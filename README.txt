-----------------------------------------------------
===  CS 6360:  Database Files and Indexing ===
-----------------------------------------------------
Name: Mahesh Shanbhag
Email: mrs160630@utdallas.edu
NetID: mrs160630



-----------------------------------------------------
== LIBRARIES AND JAVA VERSION ==
-----------------------------------------------------
Requires Java JDK version: 1.8 and above
Requires Java JRE version: 1.8 and above

Requires JAVA simple-json library for parsing. (Already included and linked in the code and the JAVA class path below.)



-----------------------------------------------------
== USAGE: HOW TO RUN THE PROGRAM / INSTALLATION ==
-----------------------------------------------------
(NOTE: PLEASE ENSURE THAT THE LOCATION OF THE DATASET IS ACCESSIBLE TO THE PROGRAM.)

To run the program the user must navigate to "src" folder where all the java files are available.



----------------------------------------
===== Installation and Execution =====
----------------------------------------

===== RUNNING VIA SCRIPT =====
There is one script available:

1. Davisbase.sh (USAGE: "bash Davisbase.sh"  OR "sh Davisbase.sh"), to run this script the following changes are required:
   a. Navigate to the 'Davisbase' folder and type 'bash Davisbase.sh' or 'sh Davisbase.sh'

   (NOTE: The script includes the compilation and running of the database. It includes the same steps as the one mentioned in the manual execution below).


===== MANUAL CODE EXECUTION ====

(NOTE THE MANUAL COMPILATIONS AND CODE EXECUTION MUST FOLLOW THE BELOW STEPS IN ORDER)
    1. Please navigate to the 'Davisbase' folder.

    2. The following commands should be executed IN ORDER as PROVIDED.

      # Delete previously compiled .class files directory.
      1.  find ./* -type f -path "./*/*.class" | xargs -n 1 rm
      2.  rm -rf ./output

      # Directory where the output .class files are written.
      3.  mkdir output

      # Include the output file directory in the classpath and export the java class path.
      4.  export CLASSPATH=./output:./lib/json-simple.jar-0.30.jar

      # Compile the code including the classpath 'CLASSPATH'
      5.  javac -classpath $CLASSPATH -d ./output ./*/*/*.java

      # Run the 'UserPrompt' class
      6.  java Main.UserPrompt



----------------------------------------
===== COMMANDS =====
----------------------------------------

The following COMMANDS are AVAILABLE in the database:
  
  1.  USE DATABASE database_name;                      Changes current database.

  2.  CREATE DATABASE database_name;                   Creates an empty database.

  3.  SHOW DATABASES;                                  Displays all databases.

  4.  DROP DATABASE database_name;                     Remove database.

  5.  SHOW TABLES;                                     Displays all tables in current database.

  6.  DESC|DESCRIBE table_name;                        Displays table schema.

  7.  CREATE TABLE table_name (                        Creates a table in current database.
      <column_name> <datatype> [PRIMARY KEY / NOT NULL]
      ...);

  8.  DROP TABLE table_name;                           Remove table data and its schema.

  9.  SELECT <column_list> FROM table_name             Display records whose rowid is <id>.
      [WHERE rowid = <value>];

  10. INSERT INTO table_name                           Inserts a record into the table.
      [(<column1>, ...)] VALUES (<value1>, <value2>, ...);

  11. DELETE FROM table_name [WHERE condition];        Deletes a record from a table.

  12. UPDATE table_name SET <conditions>               Updates a record from a table.
      [WHERE condition];

  13. VERSION;                                         Show the program version.

  14. HELP;                                            Show this help information

  15. EXIT or QUIT;                                    Exit the program



------------------------------------------------------
===== MORE COMMANDS (NOT IN THE REQUIREMENTS) =====
------------------------------------------------------
THE FOLLOWING EXTRA COMMANDS ARE IMPLEMENTED

1.  USE DATABASE database_name;                      Changes current database.

2.  SHOW DATABASES;                                  Displays all databases.

3.  DROP DATABASE database_name;                     Remove database.

4.  DESC|DESCRIBE table_name;                        Displays table schema.

5.  CREATE DATABASE database_name;                   Creates an empty database.



----------------------------------------------------------
===== Implementation Details and KNOWN SYSTEM BUGS=====
----------------------------------------------------------

  The below are some DETAILS of the database:

  a. All queries are CASEINSENSITIVE.
  b. Some time the systems misses data in the system catalog tables and columns. In this case the database may behave abruptly. SHOULD THE NEED ARISE PLEASE DELETED THE 'data' FOLDER COMPLETELY LOCATED IN THE FOLDER 'Davisbase' i.e. 'Davisbase/data'.


