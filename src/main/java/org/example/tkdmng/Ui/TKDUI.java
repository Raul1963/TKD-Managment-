package org.example.Ui;

import org.example.Controller.TKDController;
import org.example.Exceptions.DatabaseException;
import org.example.Exceptions.EntityNotFoundException;
import org.example.Exceptions.ValidationException;
import org.example.Model.*;
import org.example.Repository.*;
import org.example.Service.TKD_Service;

import java.io.IOException;
import java.security.Provider;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * The main UI class that provides a command line interface for interacting with tha TKD management system
 */
public class TKDUI {
    //private final TKDController tkdController;
    public TKDController tkdController;
    private final Scanner scanner;

    IRepo<Student> studentRepo;
    IRepo<Parent> parentRepo;
    IRepo<Session> sessionRepo;
    IRepo<Trainer> trainerRepo;
    IRepo<Contest> contestRepo;
    IRepo<TrainingCamp> trainingCampRepo;
    IRepo<BeltExam> beltExamRepo;
    /**
     * Constructor for UI, creates just a new Scanner
     */
    public TKDUI(){
          this.scanner = new Scanner(System.in);
    }

    /**
     * setter for controller
     * @param tkdController it gives the specific controller
     */
    public void setTkdController(TKDController tkdController) {
        this.tkdController = tkdController;
    }

    /**
     * Start Repo where user can choose between the repos
     */
    public TKD_Service startRepo() throws DatabaseException {
        boolean continueLoop = true;

        while(continueLoop){
            printRepo();
            String option = scanner.nextLine();
            switch (option){
                case "memory" -> {
                    continueLoop = false;
                    return inMemoryRepo();
                }
                case "file" -> {
                    continueLoop = false;
                    return inFileRepo();
                }
                case "database" -> {
                    continueLoop = false;
                    return databaseRepo();
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }

        return null;
    }



    //////// DATABASE REPOSITORY /////////

    /**
     * makes a repo for Database
     * @return a Service that contains Database Repos
     * @throws DatabaseException when it can't connect to Database
     */
    private TKD_Service databaseRepo() throws DatabaseException {
        DatabaseRepo<Student> studentRepo = new DatabaseStudent("jdbc:sqlserver://localhost:1433;database=TKD-Management;integratedSecurity=true;trustServerCertificate=true;");
        DatabaseRepo<Parent> parentRepo = new DatabaseParent("jdbc:sqlserver://localhost:1433;database=TKD-Management;integratedSecurity=true;trustServerCertificate=true;");
        DatabaseRepo<Session> sessionRepo = new DatabaseSession("jdbc:sqlserver://localhost:1433;database=TKD-Management;integratedSecurity=true;trustServerCertificate=true;");
        DatabaseRepo<Contest> contestRepo = new DatabaseContest("jdbc:sqlserver://localhost:1433;database=TKD-Management;integratedSecurity=true;trustServerCertificate=true;");
        DatabaseRepo<Trainer> trainerRepo = new DatabaseTrainer("jdbc:sqlserver://localhost:1433;database=TKD-Management;integratedSecurity=true;trustServerCertificate=true;");
        DatabaseRepo<BeltExam> beltExamRepo = new DatabaseBeltExam("jdbc:sqlserver://localhost:1433;database=TKD-Management;integratedSecurity=true;trustServerCertificate=true;");
        DatabaseRepo<TrainingCamp> trainingCampRepo = new DatabaseTrainingCamp("jdbc:sqlserver://localhost:1433;database=TKD-Management;integratedSecurity=true;trustServerCertificate=true;");


        try {
            DriverManager.getConnection("jdbc:sqlserver://localhost:1433;database=TKD-Management;integratedSecurity=true;trustServerCertificate=true;");
        } catch (SQLException e) {
            System.out.println("Error: "+e.getMessage());
        }

        return new TKD_Service(studentRepo,trainerRepo,parentRepo,sessionRepo,contestRepo,trainingCampRepo,beltExamRepo);

    }

    //////// IN MEMORY REPOSITORY /////////

    /**
     * makes a repo for Memory
     * @return a Service that contains Memory Repos
     */
    private TKD_Service inMemoryRepo(){
        InMemoryRepo<Student> studentRepo = new InMemoryRepo<>();
        InMemoryRepo<Parent> parentRepo = new InMemoryRepo<>();
        InMemoryRepo<Session> sessionRepo = new InMemoryRepo<>();
        InMemoryRepo<Trainer> trainerRepo = new InMemoryRepo<>();
        InMemoryRepo<Contest> contestRepo = new InMemoryRepo<>();
        InMemoryRepo<TrainingCamp> trainingCampRepo = new InMemoryRepo<>();
        InMemoryRepo<BeltExam> beltExamRepo = new InMemoryRepo<>();

        return new TKD_Service(studentRepo,trainerRepo,parentRepo,sessionRepo,contestRepo,trainingCampRepo,beltExamRepo);
    }

    //////// IN FILE REPOSITORY /////////
    /**
     * makes a repo for Files
     * @return a Service that contains File Repos
     */
    private TKD_Service inFileRepo(){
        InFileRepo<Student> studentRepo = new InFileRepo<>("src/main/java/org/example/Data/students.csv",Student::fromCSV);
        InFileRepo<Parent> parentRepo = new InFileRepo<>("src/main/java/org/example/Data/parents.csv",Parent::fromCSV);
        InFileRepo<Session> sessionRepo = new InFileRepo<>("src/main/java/org/example/Data/sessions.csv",Session::fromCSV);
        InFileRepo<Trainer> trainerRepo = new InFileRepo<>("src/main/java/org/example/Data/trainers.csv",Trainer::fromCSV);
        InFileRepo<Contest> contestRepo = new InFileRepo<>("src/main/java/org/example/Data/contests.csv",Contest::fromCSV);
        InFileRepo<TrainingCamp> trainingCampRepo = new InFileRepo<>("src/main/java/org/example/Data/trainingCamps.csv",TrainingCamp::fromCSV);
        InFileRepo<BeltExam> beltExamRepo = new InFileRepo<>("src/main/java/org/example/Data/beltExams.csv",BeltExam::fromCSV);

        return new TKD_Service(studentRepo,trainerRepo,parentRepo,sessionRepo,contestRepo,trainingCampRepo,beltExamRepo);
    }

    /**
     * Starts the UI application displaying a menu and handling user input
     */
    public void start() {
        boolean continueLoop = true;

        while (continueLoop) {
            printMenu();
            String option = scanner.nextLine();

            try {
                switch (option) {
                    case "0" -> continueLoop = false;
                    case "1" -> startPeopleMenu();
                    case "2" -> startEventsMenu();
                    case "3" -> generateBill();
                    case "4" -> continueLoop=false;
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * Display Ui menu for People
     */
    private void startPeopleMenu() {
        boolean contLoop1 = true;
        while (contLoop1) {
            displayPeople();
            String option1 = scanner.nextLine();

            switch (option1) {
                case "a" -> startStudent();
                case "b" -> startParent();
                case "c" -> startTrainer();
                case "d" -> contLoop1 = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Display Ui menu for Events
     */
    private void startEventsMenu() throws ValidationException {
        boolean contLoop2 = true;

        while (contLoop2) {
            displayEvents();
            String option2 = scanner.nextLine();

            switch (option2) {
                case "a" -> startSession();
                case "b" -> startContest();
                case "c" -> startTrainingCamp();
                case "d" -> startBeltExam();
                case "e" -> combinationsOfEvents();
                case "f" -> contLoop2 = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Display Ui menu for Repos
     */
    private static void printRepo(){
        System.out.println("\n==== TKD Management Console ====");
        System.out.println("\n Select Repo you want to use: ");
        System.out.println("\n 1. InMemoryRepo (type 'memory')");
        System.out.println("\n 2. InFileRepo (type 'file')");
        System.out.println("\n 3. DatabaseRepo (type 'database')");
        System.out.println("\n Select an option: ");
    }

    /**
     * display an interactive menu
     */
    private void printMenu() {
        System.out.println("\n==== TKD Management Console ====");
        System.out.println("1 - Manage People");
        System.out.println("2 - Manage Sessions and Events");
        System.out.println("3 - Billing");
        System.out.println("4 - Exit");
        System.out.print("Select an option: ");
    }

    /**
     * Display a menu for people
     */
    private void displayPeople(){
        System.out.println("\ta. Students");
        System.out.println("\tb. Parents");
        System.out.println("\tc. Trainers");
        System.out.println("\td. Exit");
        System.out.print("Select an option: ");
    }

    /**
     * Display a menu for events
     */
    private void displayEvents(){
        System.out.println("\ta. Sessions");
        System.out.println("\tb. Contests");
        System.out.println("\tc. Training Camps");
        System.out.println("\td. Belt Exams");
        System.out.println("\te. Affordable events");
        System.out.println("\tf. Exit");
        System.out.print("Select an option: ");
    }



    /**
     * display all combinations that a parent can afford with a specific amount of money
     */
    private void combinationsOfEvents() throws ValidationException{
        System.out.println("Enter the amount of money: ");
        int amountOfMoney;
        try {
            amountOfMoney = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("amountOfMoney can't be empty");
        }

        System.out.println("Here is a list of events (contests and/or training camps) within the budget range you specified:");
        tkdController.eventsThatdontExceedAmountOfMoney(amountOfMoney);

    }

    /**
     * menu for a student
     */
    private void startStudent(){
        boolean continueLoop = true;

        while (continueLoop) {
            printStudent();
            String option = scanner.nextLine();

            try {
                switch (option) {
                    case "0" -> continueLoop = false;
                    case "1" -> addStudent();
                    case "2" -> deleteStudent();
                    case "3" -> viewStudents();
                    case "4" -> changeStudentSession();
                    case "5" -> viewAttendances();
                    case "6" -> sortStudentsByAttend();
                    case "7" -> filterStudentsByBeltLevel();
                    case "8" -> sortStudentsAlphabetical();
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * menu for a parent
     */
    private void startParent(){
        boolean continueLoop = true;
        while (continueLoop){
            printParent();
            String option = scanner.nextLine();
            try {
                switch (option) {
                    case "0" -> continueLoop = false;
                    case "1" -> deleteParent();
                    case "2" -> viewParents();
                    case "3" -> filterParentsByNumberOfChildren();
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * menu for a trainer
     */
    private void startTrainer(){
        boolean continueLoop = true;

        while (continueLoop) {
            printTrainer();
            String option = scanner.nextLine();

            try {
                switch (option) {
                    case "0" -> continueLoop = false;
                    case "1" -> addTrainer();
                    case "2" -> deleteTrainer();
                    case "3" -> viewTrainers();
                    case "4" -> assignSessionToTrainer();
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * menu for a session
     */
    private void startSession(){
        boolean continueLoop = true;

        while (continueLoop) {
            printSession();
            String option = scanner.nextLine();

            try {
                switch (option) {
                    case "0" -> continueLoop = false;
                    case "1" -> addSession();
                    case "2" -> deleteSession();
                    case "3" -> changeStudentSession();
                    case "4" -> assignSessionToTrainer();
                    case "5" -> addAttendance();
                    case "6" -> sortSessionByNumberOfParticipants();
                    case "7" -> getMostProfitableDateForSession();
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * menu for a contest
     */
    private void startContest(){
        boolean continueLoop = true;

        while (continueLoop) {
            printContest();
            String option = scanner.nextLine();

            try {
                switch (option) {
                    case "0" -> continueLoop = false;
                    case "1" -> addContest();
                    case "2" -> deleteContest();
                    case "3" -> viewContests();
                    case "4" -> addStudentToContest();
                    case "5" -> sortContestsBuStartDate();
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * menu for belt exam
     */
    private void startBeltExam(){
        boolean continueLoop = true;

        while (continueLoop) {
            printBeltExam();
            String option = scanner.nextLine();

            try {
                switch (option) {
                    case "0" -> continueLoop = false;
                    case "1" -> addBeltExam();
                    case "2" -> deleteBeltExam();
                    case "3" -> viewBeltExams();
                    case "4" -> addStudentToBeltExam();
                    case "5" -> addResultToBeltExam();
                    case "6" -> sortBeltExamnsByDates();
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * menu for a Training camp
     */
    private void startTrainingCamp(){
        boolean continueLoop = true;

        while (continueLoop) {
            printTrainingCamp();
            String option = scanner.nextLine();

            try {
                switch (option) {
                    case "0" -> continueLoop = false;
                    case "1" -> addTrainingCamp();
                    case "2" -> deleteTrainingCamp();
                    case "3" -> viewTrainingCamp();
                    case "4" -> addStudentToTrainingCamp();
                    case "5" -> sortedCampsByDates();
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * CRUD for parents menu
     */
    private void printParent(){
        System.out.println("\n==== TKD Management Console ====");
        System.out.println("1 - Delete Parent");
        System.out.println("2 - View Parents");
        System.out.println("3 - Filter Parents by number of children");
        System.out.println("0 - Exit");
        System.out.print("Select an option: ");
    }

    /**
     * CRUD for Students menu
     */
    private void printStudent(){
        System.out.println("\n==== TKD Management Console ====");
        System.out.println("1 - Add Student");
        System.out.println("2 - Delete Student");
        System.out.println("3 - View Students");
        System.out.println("4 - Change Student Session");
        System.out.println("5 - View Student attendances");
        System.out.println("6 - View sorted students by attendances");
        System.out.println("7 - View students filtered by belt level");
        System.out.println("8 - Sort students ordered alphabetical");
        System.out.println("0 - Exit");
        System.out.print("Select an option: ");
    }

    /**
     * CRUD for Trainers menu
     */
    private void printTrainer(){
        System.out.println("\n==== TKD Management Console ====");
        System.out.println("1 - Add Trainer");
        System.out.println("2 - Delete Trainer");
        System.out.println("3 - View Trainers");
        System.out.println("4 - Assign Session to Trainer");
        System.out.println("0 - Exit");
        System.out.print("Select an option: ");
    }

    /**
     * CRUD for Sessions menu
     */
    private void printSession(){
        System.out.println("\n==== TKD Management Console ====");
        System.out.println("1 - Add Session");
        System.out.println("2 - Delete Session");
        System.out.println("3 - Change Student Session");
        System.out.println("4 - Assign Session to Trainer");
        System.out.println("5 - Add attendance");
        System.out.println("6 - Sort session by number of participants");
        System.out.println("7 - Get date with the most students that attended a given session");
        System.out.println("0 - Exit");
        System.out.print("Select an option: ");
    }

    /**
     * CRUD for Contests menu
     */
    private void printContest(){
        System.out.println("\n==== TKD Management Console ====");
        System.out.println("1 - Add Contest");
        System.out.println("2 - Delete Contest");
        System.out.println("3 - View all Contests");
        System.out.println("4 - Add student to contest");
        System.out.println("5 - View sorted contests by start date");
        System.out.println("0 - Exit");
        System.out.print("Select an option: ");
    }

    /**
     * CRUD for TrainingCamps menu
     */
    private void printTrainingCamp(){
        System.out.println("\n==== TKD Management Console ====");
        System.out.println("1 - Add Training camp");
        System.out.println("2 - Delete training camp");
        System.out.println("3 - View all training camps");
        System.out.println("4 - Add student to training camp");
        System.out.println("5 - Sorted Camps by starting dates");
        System.out.println("0 - Exit");
        System.out.print("Select an option: ");
    }

    /**
     * CRUD for BeltExams menu
     */
    private void printBeltExam(){
        System.out.println("\n==== TKD Management Console ====");
        System.out.println("1 - Add Belt Exam");
        System.out.println("2 - Delete Belt Exam");
        System.out.println("3 - View all Belt Exams");
        System.out.println("4 - Add student to Belt Exam");
        System.out.println("5 - Add result to Belt Exam");
        System.out.println("6 - Sort Belt Examines by starting dates");
        System.out.println("0 - Exit");
        System.out.print("Select an option: ");
    }

    //////////// METHODS FOR EACH OPTION /////////////

    /**
     * request all information that a student need.
     * @throws ValidationException if a specific input doesn't match the specific requests
     */
    private void addStudent() throws ValidationException{
        int idStudent = tkdController.getStudentId();
        System.out.print("Enter student first name: ");
        String name = scanner.nextLine();
        if (name.isEmpty() || name.length()==1) {
            throw new ValidationException("student first name cannot be empty or have one letter only");
        }
        if(!name.matches("^[a-zA-Z]+$")){
            throw new ValidationException("student name must contain only letters");
        }


        System.out.print("Enter student last name: ");
        String lastName = scanner.nextLine();
        if (lastName.isEmpty()) {
            throw new ValidationException("student last name cannot be empty or have one letter only");
        }
        if(!lastName.matches("^[a-zA-Z]+$")){
            throw new ValidationException("student last name must contain only letters");
        }
        System.out.print("Enter student email: ");
        String email = scanner.nextLine();
        if (email.isEmpty()) {
            throw new ValidationException("student email cannot be empty");
        }
        if(!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")){
            throw new ValidationException("student email isn't valid");
        }


        System.out.print("Enter student address: ");
        String address = scanner.nextLine();
        if (address.isEmpty()) {
            throw new ValidationException("student address cannot be empty");
        }
        if(!address.matches("^[a-zA-Z]+\\s+\\d+$")){
            throw new ValidationException("student address isn't valid");
        }

        System.out.print("Enter student year of birth: ");
        int dateOfBirth;
        try {
            dateOfBirth = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("dateOfBirth can't be empty or contain letters");
        }

        if (dateOfBirth < 2006) {
            throw new ValidationException("student year of birth cannot be smaller than 2006");
        }

        System.out.print("Enter student telephone number: ");
        String telNumber = scanner.nextLine();
        if (telNumber.isEmpty()) {
            throw new ValidationException("student telephone number cannot be empty");
        }
        if(!telNumber.matches("^0\\d+$") || telNumber.length() != 10){
            throw new ValidationException("student number isn't valid");
        }


        System.out.print("Enter student belt level: ");
        BeltLevel beltLevel;
        try{
            beltLevel = BeltLevel.valueOf(scanner.nextLine());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid belt level");
        }
        System.out.print("Enter session ID: ");
        int sessionId;
        try {
            sessionId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("sessionId can't be empty or contain letters");
        }

        if (sessionId == 0 || sessionId < 0) {
            throw new ValidationException("session ID cannot be null or negative");
        }

        Session session = null;
        session = tkdController.getSessionById(sessionId);
        Student student = new Student(idStudent, name, lastName, email, address, dateOfBirth, telNumber, beltLevel, session.getId());
        tkdController.addObject(student);
        System.out.println("Every student needs a parent or a contact person:");
        Parent parent = addParent();


        //call addStudentToParent
        tkdController.addStudentToParent(student, parent);

        // call addStudentToSession function
        tkdController.addStudentToSession(sessionId, idStudent);

        //tkdController.addStudent(student);

        System.out.println("Student added successfully.");
    }

    /**
     ** request all information that a parent need
     *  @return a Parent object
     * @throws ValidationException if a specific input doesn't match the specific requests
     */
    private Parent addParent() throws ValidationException {
        int id = tkdController.getParentId();
        System.out.print("Enter parent first name: ");
        String name = scanner.nextLine();
        if(name.isEmpty() || name.length()==1){
            throw new ValidationException("parent first name cannot be empty or have one letter only");
        }
        if(!name.matches("^[a-zA-Z]+$")){
            throw new ValidationException("parent first name must contain only letters");
        }

        System.out.print("Enter parent last name: ");
        String lastName = scanner.nextLine();
        if(lastName.isEmpty() || lastName.length()==1){
            throw new ValidationException("parent last name cannot be empty or have one letter only");
        }
        if(!lastName.matches("^[a-zA-Z]+$")){
            throw new ValidationException("parent last name must contain only letters");
        }

        System.out.print("Enter parent email: ");
        String email = scanner.nextLine();
        if(email.isEmpty()){
            throw new ValidationException("parent email cannot be empty");
        }
        if(!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")){
            throw new ValidationException("parent email isn't valid");
        }

        System.out.print("Enter parent address: ");
        String address = scanner.nextLine();
        if(address.isEmpty() || address.length()==1){
            throw new ValidationException("parent address cannot be empty or have one letter only");
        }
        if(!address.matches("^[a-zA-Z]+\\s+\\d+$")){
            throw new ValidationException("parent address isn't valid");
        }
        System.out.print("Enter parent number: ");
        String number = scanner.nextLine();
        if(number.isEmpty()){
            throw new ValidationException("parent number cannot be empty");
        }
        if(!number.matches("^0\\d+$") || number.length() != 10){
            throw new ValidationException("parent number isn't valid");
        }
        Parent newParent = new Parent(id, name, lastName, email, address,number);
        return newParent;
    }

    /**
     * request all information that a session needs.
     * @throws ValidationException if a specific input doesn't match the specific requests
     */
    private void addSession() throws ValidationException{
        int id = tkdController.getSessionId();
        System.out.print("Enter Difficulty Level (beginner, intermediary, advanced): ");
        DifficultyLevel difficultyLevel;
        try {
            difficultyLevel = DifficultyLevel.valueOf(scanner.nextLine());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid difficulty level. Please enter beginner, intermediary, advanced.");
        }

        System.out.print("Enter Maximum Number of Participants: ");
        int maximumParticipants;
        try {
            maximumParticipants = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("maximumParticipants can't be empty or contain letters ");
        }

        if(maximumParticipants == 0 || maximumParticipants < 0){
            throw new ValidationException("Maximum Number of Participants cannot be null or negative");
        }

        System.out.print("Enter Trainer ID for this Session: ");
        int trainerId;
        try {
            trainerId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("trainerId can't be empty or contain letters");
        }

        if(trainerId == 0 || trainerId < 0){
            throw new ValidationException("Trainer ID for this Session cannot be null or negative");
        }

        Trainer trainer = null;
        trainer = tkdController.getTrainerById(trainerId);

        System.out.print("Enter Price Per Session: ");
        double pricePerSession;
        try {
            pricePerSession = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("price can't be empty or contain letters");
        }

        if(pricePerSession == 0 || pricePerSession < 0){
            throw new ValidationException("Price for this Session cannot be null or negative");
        }

        // Crearea obiectului Session
        Session session = new Session(id, difficultyLevel, maximumParticipants, trainer.getId(), pricePerSession);
        tkdController.addObject(session);
        System.out.println("Session added successfully.");
    }

    /**
     * request all information that a trainer need.
     * @throws ValidationException if a specific input doesn't match the specific requests
     */
    private void addTrainer() throws ValidationException {
        int id = tkdController.getTrainerId();

        System.out.print("Enter Trainer First Name: ");
        String name = scanner.nextLine();
        if(name.isEmpty() || name.length() == 1){
            throw new ValidationException("Trainer First Name cannot be empty or have one letter only");
        }
        if(!name.matches("^[a-zA-Z]+$")){
            throw new ValidationException("Trainer first name must contain only letters");
        }

        System.out.print("Enter Trainer Last Name: ");
        String lastName = scanner.nextLine();
        if(lastName.isEmpty() || lastName.length() == 1){
            throw new ValidationException("Trainer Last Name cannot be empty or have one letter only");
        }
        if(!lastName.matches("^[a-zA-Z]+$")){
            throw new ValidationException("Trainer last name must contain only letters");
        }

        System.out.print("Enter Trainer Email: ");
        String email = scanner.nextLine();
        if(email.isEmpty()){
            throw new ValidationException("Trainer Email cannot be empty");
        }
        if(!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")){
            throw new ValidationException("trainer email isn't valid");
        }

        System.out.print("Enter Trainer Address: ");
        String address = scanner.nextLine();
        if(address.isEmpty()){
            throw new ValidationException("Trainer Address cannot be empty");
        }
        if(!address.matches("^[a-zA-Z]+\\s+\\d+$")){
            throw new ValidationException("trainer address isn't valid");
        }

        System.out.print("Enter Trainer Year of Birth: ");
        int dateOfBirth;
        try {
            dateOfBirth = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("dateOfBirth can't be empty or contain letters");
        }

        if(dateOfBirth <  1964){
            throw new ValidationException("Trainer Year of Birth cannot be smaller than 1964");
        }

        System.out.print("Enter Trainer Telephone Number: ");
        String number = scanner.nextLine();
        if(number.isEmpty()){
            throw new ValidationException("Trainer Telephone Number cannot be empty");
        }
        if(!number.matches("^0\\d+$") || number.length() != 10){
            throw new ValidationException("trainer number isn't valid");
        }

        System.out.print("Enter Trainer Belt Level: ");
        BeltLevel beltLevel;
        try{
            beltLevel = BeltLevel.valueOf(scanner.nextLine());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid belt level");
        }

        Trainer newTrainer = new Trainer(id, name, lastName, email, address, dateOfBirth, number, beltLevel);

        tkdController.addObject(newTrainer);
        System.out.println("Trainer added successfully.");
    }

    /**
     * request all information that a contest need.
     * @throws ValidationException if a specific input doesn't match the specific requests
     */
    private void addContest() throws ValidationException {
        int id = tkdController.getContestId();

        System.out.print("Enter start date: ");
        String startdate = scanner.nextLine();
        if(startdate.isEmpty()){
            throw new ValidationException(" start date cannot be empty");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(startdate, formatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException("start date doesn't have correct format");
        }

        System.out.print("Enter end date: ");
        String enddate = scanner.nextLine();
        if(enddate.isEmpty()){
            throw new ValidationException(" end date cannot be empty");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(enddate, formatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException("end date doesn't have correct format");
        }

        System.out.println("Enter price: ");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("Price can't be empty or contain letters");
        }
        if(price == 0 || price < 0){
            throw new ValidationException("Price cannot be null or negative");
        }

        System.out.print("Enter country: ");
        String country = scanner.nextLine();
        if(country.isEmpty() || country.length() == 1){
            throw new ValidationException("country cannot be empty or have one letter only");
        }
        if(!country.matches("^[a-zA-Z]+$")){
            throw new ValidationException("country isn't valid");
        }

        System.out.print("Enter city: ");
        String city = scanner.nextLine();
        if(city.isEmpty() || city.length() == 1){
            throw new ValidationException("city cannot be empty or have one letter only");
        }
        if(!city.matches("^[a-zA-Z]+$")){
            throw new ValidationException("city isn't valid");
        }
        System.out.print("Enter address: ");
        String address = scanner.nextLine();

        if(address.isEmpty()){
            throw new ValidationException("address cannot be empty");
        }
        if(!address.matches("^[a-zA-Z]+\\s+\\d+$")){
            throw new ValidationException("contest address isn't valid");
        }

        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        if(name.isEmpty() || name.length() == 1){
            throw new ValidationException("name cannot be empty or have one letter only");
        }
        if(!name.matches("^[a-zA-Z]+$")){
            throw new ValidationException("Contest name must contain only letters");
        }

        Contest contest = new Contest(id,startdate,enddate,price, country, city, name,address);
        tkdController.addObject(contest);
        System.out.println("Contest added successfully.");
    }

    /**
     * request all information that a training camp need.
     * @throws ValidationException if a specific input doesn't match the specific requests
     */
    private void addTrainingCamp() throws ValidationException {
        int id = tkdController.getTrainingCampId();

        System.out.print("Enter start date: ");
        String startdate = scanner.nextLine();
        if(startdate.isEmpty()){
            throw new ValidationException(" start date cannot be empty");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(startdate, formatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException("start date doesn't have correct format");
        }


        System.out.print("Enter end date: ");
        String enddate = scanner.nextLine();

        if(enddate.isEmpty()){
            throw new ValidationException("end date cannot be empty");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(enddate, formatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException("end date doesn't have correct format");
        }

        System.out.println("Enter price date: ");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("Price can't be empty or contain letters");
        }
        if(price == 0 || price < 0){
            throw new ValidationException("Price cannot be null or negative");
        }

        System.out.print("Enter country: ");
        String country = scanner.nextLine();
        if(country.isEmpty() || country.length() == 1){
            throw new ValidationException("country cannot be empty or have one letter only");
        }
        if(!country.matches("^[a-zA-Z]+$")){
            throw new ValidationException("country isn't valid");
        }


        System.out.print("Enter city: ");
        String city = scanner.nextLine();
        if(city.isEmpty() || city.length() == 1){
            throw new ValidationException("city cannot be empty or have one letter only");
        }
        if(!city.matches("^[a-zA-Z]+$")){
            throw new ValidationException("city isn't valid");
        }


        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        if(address.isEmpty()){
            throw new ValidationException("address cannot be empty");
        }
        if(!address.matches("^[a-zA-Z]+\\s+\\d+$")){
            throw new ValidationException("training camp address isn't valid");
        }

        System.out.println("Enter max number of students: ");
        int numberOfParticipants;
        try {
            numberOfParticipants = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("numberOfParticipants can't be empty or contain letters");
        }

        TrainingCamp trainingCamp = new TrainingCamp(id,startdate,enddate,price, country, city, address, numberOfParticipants);
        tkdController.addObject(trainingCamp);
        System.out.println("Training camp added successfully.");
    }

    /**
     * request all information that a BeltExam need.
     * @throws ValidationException if a specific input doesn't match the specific requests
     */
    private void addBeltExam() throws ValidationException {
        int id = tkdController.getBeltExamId();
        System.out.print("Enter start date: ");
        String startdate = scanner.nextLine();
        if(startdate.isEmpty()){
            throw new ValidationException(" start date cannot be empty");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(startdate, formatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException("start date doesn't have correct format");
        }

        System.out.print("Enter end date: ");
        String enddate = scanner.nextLine();
        if(enddate.isEmpty()){
            throw new ValidationException(" end date cannot be empty");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(enddate, formatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException("end date doesn't have correct format");
        }


        System.out.println("Enter price date: ");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine());
        }catch (NumberFormatException e){
            throw new ValidationException("price can't be empty or contain letters");
        }
        if(price == 0 || price < 0){
            throw new ValidationException("Price cannot be null or negative");
        }

        System.out.print("Enter country: ");
        String country = scanner.nextLine();
        if(country.isEmpty() || country.length() == 1){
            throw new ValidationException("country cannot be empty or have one letter only");
        }
        if(!country.matches("^[a-zA-Z]+$")){
            throw new ValidationException("country isn't valid");
        }

        System.out.print("Enter city: ");
        String city = scanner.nextLine();
        if(city.isEmpty() || city.length() == 1){
            throw new ValidationException("city cannot be empty or have one letter only");
        }
        if(!city.matches("^[a-zA-Z]+$")){
            throw new ValidationException("city isn't valid");
        }


        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        if(address.isEmpty()){
            throw new ValidationException("address cannot be empty");
        }
        if(!address.matches("^[a-zA-Z]+\\s+\\d+$")){
            throw new ValidationException("belt exam address isn't valid");
        }


        System.out.print("Enter belt color: ");
        BeltLevel beltColor;
        try{
            beltColor = BeltLevel.valueOf(scanner.nextLine());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid belt level");
        }

        BeltExam beltExam = new BeltExam(id,startdate,enddate,price, country, city, address, beltColor);
        tkdController.addObject(beltExam);
        System.out.println("Belt Exam added successfully.");
    }

    /**
     * request all information that a belt exam need.
     * @throws ValidationException if a specific input doesn't match the specific requests
     */
    private void addStudentToBeltExam() throws ValidationException {
        System.out.println("Enter Belt Exam Id: ");
        int idBeltExam;
        try {
            idBeltExam = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("idBeltExam can't be empty or contain letters");
        }
        if(idBeltExam == 0 || idBeltExam < 0){
            throw new ValidationException("Belt exam ID cannot be null or negative");
        }

        System.out.println("Enter Student Id: ");
        int studentId;
        try {
            studentId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("studentId can't be empty or contain letters");
        }
        if(studentId == 0 || studentId < 0){
            throw new ValidationException("Student id ID cannot be null or negative");
        }

        tkdController.addStudentToBeltExam(idBeltExam,studentId);
        System.out.println("Student added successfully.");
    }

    /**
     * add a student to contest based on their ID s.
     * @throws ValidationException if a specific input doesn't match the specific requests
     */
    private void addStudentToContest() throws ValidationException {
        System.out.println("Enter Contest Id: ");
        int idContest;
        try {
            idContest = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("idContest can't be empty or contain letters");
        }
        if(idContest == 0 || idContest < 0){
            throw new ValidationException("Contest ID cannot be null or negative");
        }

        System.out.println("Enter Student Id: ");
        int studentId;
        try {
            studentId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("studentId can't be empty or contain letters");
        }
        if(studentId == 0 || studentId < 0){
            throw new ValidationException("Student ID cannot be null or negative");
        }

        tkdController.addStudentToContest(studentId,idContest);
        System.out.println("Student added successfully.");
    }

    /**
     * add a student to a training camp based on their ID s.
     * @throws ValidationException if a specific input doesn't match the specific requests
     */
    private void addStudentToTrainingCamp() throws ValidationException {
        System.out.println("Enter Student Id: ");
        int idStudent;
        try {
            idStudent = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("idStudent can't be empty or contain letters");
        }
        if(idStudent == 0 || idStudent < 0){
            throw new ValidationException("Student ID cannot be null or negative");
        }

        System.out.println("Enter Training Camp Id: ");
        int idTrainingCamp;
        try {
            idTrainingCamp = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("idTrainingCamp can't be empty or contain letters");
        }
        if(idTrainingCamp == 0 || idTrainingCamp < 0){
            throw new ValidationException("Training camp ID cannot be null or negative");
        }

        tkdController.addStudentToTrainingCamp(idStudent,idTrainingCamp);
        System.out.println("Student added successfully.");
    }

    /**
     * deletes a student base on ID.

     */
    private void deleteStudent() throws ValidationException {
        int id = readStudentId();
        tkdController.deleteStudent(id);
        System.out.println("Student deleted successfully.");
    }

    /**
     * deletes a belt exam base on ID.
     */
    private void deleteBeltExam() throws ValidationException {
        int id = readBeltExamId();
        tkdController.deleteBeltExam(id);
        System.out.println("Belt Exam deleted successfully.");
    }

    /**
     * deletes a training camp base on ID.
     */
    private void deleteTrainingCamp() throws ValidationException {
        int id = readTrainingCampId();
        tkdController.deleteTrainingCamp(id);
        System.out.println("Training camp deleted successfully.");
    }

    /**
     * deletes a parent base on ID.
     */
    private void deleteParent() throws ValidationException {
        int id = readParentId();
        tkdController.deleteParent(id);
        System.out.println("Parent deleted successfully.");
    }

    /**
     * deletes a trainer base on ID.
     */
    private void deleteTrainer() throws ValidationException {
        int id = readTrainerId();
        tkdController.deleteTrainer(id);
        System.out.println("Trainer deleted successfully.");
    }

    /**
     * deletes a session base on ID.
     */
    private void deleteSession() throws ValidationException{
        int id = readSessionId();
        tkdController.deleteSession(id);
        System.out.println("Session deleted successfully.");
    }

    /**
     * deletes a contest base on ID.
     */
    private void deleteContest() throws ValidationException {
        int id = readContestId();
        tkdController.deleteContest(id);
        System.out.println("Contest deleted successfully.");
    }

    /**
     * displays a list of trainers
     */
    private void viewTrainers() {
        System.out.println("=== List of Trainers ===");
        tkdController.viewTrainers();
    }

    /**
     * displays a list of parents
     */
    private void viewParents(){
        System.out.println("=== List of Parents ===");
        tkdController.viewParents();
    }

    /**
     * displays a list of contests
     */
    private void viewContests(){
        System.out.println("=== List of Contests ===");
        tkdController.viewContests();
    }

    /**
     * displays a list of training camps
     */
    private void viewTrainingCamp(){
        System.out.println("=== List of Training camps ===");
        tkdController.viewTrainingCamps();
    }

    /**
     * displays a list of students
     */
    private void viewStudents() {
        System.out.println("=== List of Students ===");
        tkdController.viewStudents();
    }

    /**
     * displays a list of belt exams
     */
    private void viewBeltExams(){
        System.out.println("=== List of Belt Exams ===");
        tkdController.viewBeltExams();
    }

    /**
     * displays a list of attendances for a student.
     */
    private void viewAttendances() throws ValidationException {
        int studentId = readStudentId();
        System.out.println("=== List of attendances ===");
        tkdController.numberOfAttendances(studentId);
    }



    /**
     * Changes the session of a trainer, by reading the new session id and the trainer id from the console, and displaying a successful message.
     */
    private void assignSessionToTrainer() throws ValidationException {
        int sessionId = readSessionId();
        int trainerId = readTrainerId();
        tkdController.assignSessionToTrainer(sessionId, trainerId);
        System.out.println("Session assigned to trainer successfully.");
    }
    /**
     * Adds an attendance to a student, by reading the session id, the student id, if he attended or not, the weekday and the date from the console.
     * @throws ValidationException      if a specific input doesn't match the specific requests
     */
    private void addAttendance() throws ValidationException{
        int sessionId = readSessionId();
        int studentId = readStudentId();

        System.out.print("Enter attendance(true or false): ");
        boolean attendance = Boolean.parseBoolean(scanner.nextLine());

        System.out.print("Enter week day: ");
        String weekday = scanner.nextLine();
        if(weekday.isEmpty()){
            throw new ValidationException("week day cannot be empty");
        }
        if(!weekday.matches("^[a-zA-Z]+$")){
            throw new ValidationException("weekday isn't valid");
        }

        System.out.print("Enter date: ");
        String date = scanner.nextLine();
        if(date.isEmpty()){
            throw new ValidationException("date cannot be empty");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException("date doesn't have correct format");
        }
        tkdController.addAttendance(studentId,sessionId, attendance, weekday, date);
    }

    /**
     * Changes the session of a student, by reading the new session id and the student id from the console, and displaying a successful message.
     */
    private void changeStudentSession() throws ValidationException {
        int studentId = readStudentId();
        int sessionId = readSessionId();
        tkdController.changeStudentSession(studentId, sessionId);
        System.out.println("Student session changed successfully.");
    }

    /**
     * Adds result for a student from the belt exam, by reading the student id, belt exam id and the result from the console and displaying it.
     */
    private void addResultToBeltExam() throws ValidationException {
        int studentId = readStudentId();
        int beltExamId = readBeltExamId();
        System.out.println("Result(true/false): ");
        boolean promoted = Boolean.parseBoolean(scanner.nextLine());
        tkdController.addResultToBeltExam(studentId, beltExamId, promoted);
    }

    /**
     * Generates a bill for a parent and displays it in the console.
     * @throws ValidationException      if a specific input doesn't match the specific requests
     */
    private void generateBill() throws ValidationException {
        int parentId = readParentId();

        System.out.print("Enter   the month for the invoice: ");
        String month = scanner.nextLine();
        if(month.isEmpty()){
            throw new ValidationException(" the month for the invoice cannot be empty");
        }
        if(Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12){
            throw new ValidationException("The month for the invoice must be between 1 and 12");
        }

        tkdController.generateInvoice(parentId, month);
    }

    /**
     * Reads an id for a student from the console.
     * @return The id of the student.
     * @throws ValidationException      if a specific input doesn't match the specific requests
     */
    private int readStudentId() throws ValidationException {
        System.out.print("Enter student ID: ");
        int id;
        try{
           id = Integer.parseInt(scanner.nextLine());
        }
        catch (NumberFormatException e){
            throw new ValidationException("studentId can't be empty or contain letters");
        }
        if(id == 0 || id < 0){
            throw new ValidationException("Student ID cannot be null or negative");
        }
        return id;
    }

    /**
     * Reads an id for a parent from the console.
     * @return The id of the parent.
     * @throws ValidationException      if a specific input doesn't match the specific requests
     */
    private int readParentId() throws ValidationException {
        System.out.print("Enter parent ID: ");
        int id;
        try{
            id = Integer.parseInt(scanner.nextLine());
        }
        catch (NumberFormatException e){
            throw new ValidationException("Parent id can't be empty or contain letters");
        }
        if(id == 0 || id < 0){
            throw new ValidationException("Parent ID cannot be null or negative");
        }
        return id;
    }

    /**
     * Reads an id for a trainer from the console.
     * @return The id of the trainer.
     * @throws ValidationException      if a specific input doesn't match the specific requests
     */
    private int readTrainerId() throws ValidationException {
        System.out.print("Enter trainer ID: ");
        int id;
        try{
            id = Integer.parseInt(scanner.nextLine());
        }
        catch (NumberFormatException e){
            throw new ValidationException("Trainer id can't be empty or contain letters");
        }
        if(id == 0 || id < 0){
            throw new ValidationException("Trainer ID cannot be null or negative");
        }
        return id;
    }

    /**
     * Reads an id for a session from the console.
     * @return The id of the session.
     * @throws ValidationException      if a specific input doesn't match the specific requests
     */
    private int readSessionId() throws ValidationException {
        System.out.print("Enter session ID: ");
        int id;
        try{
            id = Integer.parseInt(scanner.nextLine());
        }
        catch (NumberFormatException e){
            throw new ValidationException("Session id can't be empty or contain letters");
        }
        if(id == 0 || id < 0){
            throw new ValidationException("Session ID cannot be null or negative");
        }
        return id;
    }

    /**
     * Reads an id for a contest from the console.
     * @return The id of the contest.
     * @throws ValidationException      if a specific input doesn't match the specific requests
     */
    private int readContestId() throws ValidationException {
        System.out.print("Enter contest ID: ");
        int id;
        try{
            id = Integer.parseInt(scanner.nextLine());
        }
        catch (NumberFormatException e){
            throw new ValidationException("Contest id can't be empty or contain letters");
        }
        if(id == 0 || id < 0){
            throw new ValidationException("Contest ID cannot be null or negative");
        }
        return id;
    }

    /**
     * Reads an id for a training camp from the console.
     * @return The id of the training camp.
     * @throws ValidationException      if a specific input doesn't match the specific requests
     */
    private int readTrainingCampId() throws ValidationException {
        System.out.print("Enter training camp ID: ");
        int id;
        try{
            id = Integer.parseInt(scanner.nextLine());
        }
        catch (NumberFormatException e){
            throw new ValidationException("Training camp id can't be empty or contain letters");
        }
        if(id == 0 || id < 0){
            throw new ValidationException("Training camp ID cannot be null or negative");
        }
        return id;
    }

    /**
     * Reads an id for a belt exam from the console.
     * @return The id of the belt exam.
     * @throws ValidationException      if a specific input doesn't match the specific requests
     */
    private int readBeltExamId() throws ValidationException {
        System.out.print("Enter belt exam ID: ");
        int id;
        try{
            id = Integer.parseInt(scanner.nextLine());
        }
        catch (NumberFormatException e){
            throw new ValidationException("Belt exam id can't be empty or contain letters");
        }
        if(id == 0 || id < 0){
            throw new ValidationException("Belt exam ID cannot be null or negative");
        }
        return id;
    }

    /**
     * call the sortedContest function from Controller that prints out the contests sorted by their start date
     */
    private void sortContestsBuStartDate(){
        tkdController.sortedContests();
    }

    /**
     * call the sortedStudentsByAttend function from Controller that prints out the students sorted by their number of attendances
     */
    private void sortStudentsByAttend(){
        tkdController.sortedStudentsByAttend();
    }

    /**
     * call the filter students function from Controller that prints out the students filtered by a belt level read from the console
     * @throws ValidationException      if a specific input doesn't match the specific requests
     */
    private void filterStudentsByBeltLevel() throws ValidationException {
        System.out.print("Enter the belt level for filtering: ");
        BeltLevel beltLevel;
        try{
            beltLevel = BeltLevel.valueOf(scanner.nextLine());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid belt level");
        }

        tkdController.filteredStudentsByBeltLevel(beltLevel);
    }

    /**
     * call the filter parents function from Controller that prints out the parents filtered by a number of children read from the console.
     * @throws ValidationException      if a specific input doesn't match the specific requests
     */
    private void filterParentsByNumberOfChildren() throws ValidationException {
        System.out.println("Enter the number of children for filtering: ");
        int numberOfChildren;
        try {
            numberOfChildren = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new ValidationException("Number of children can't be empty or contain letters");
        }
        if (numberOfChildren == 0 || numberOfChildren < 0) {
            throw new ValidationException("Number of children cannot be null or negative");
        }
        tkdController.filterParentsByNumberOfChildren(numberOfChildren);
    }

    /**
     * calls the sortStudentsAlphabetical function in the controller
     */
    private void sortStudentsAlphabetical(){
        tkdController.sortStudentsAlphabetical();
    }

    /**
     * calls the sortSessionByNumberOfParticipants function in the controller
     */
    private void sortSessionByNumberOfParticipants(){
        tkdController.sortSessionByNumberOfParticipants();
    }

    /**
     * calls the sortedCampsByDates function from controller
     */
    private void sortedCampsByDates(){
        tkdController.sortedCampsByDates();
    }

    /**
     * calls the sortBeltExamsByDates function from controller
     */
    private void sortBeltExamnsByDates(){
        tkdController.sortBeltExamnsByDates();
    }

    /**
     * Reads a session id and gives it as parameter to the getDateWithMostStudentsForSession function from controller.
     */
    public void getMostProfitableDateForSession() throws ValidationException {
        int sessionId = readSessionId();
        tkdController.getMostProfitableDateForSession(sessionId);
    }
}
