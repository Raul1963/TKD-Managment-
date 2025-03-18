package org.example.Controller;

import org.example.Exceptions.BusinessLogicException;
import org.example.Exceptions.DatabaseException;
import org.example.Exceptions.EntityNotFoundException;
import org.example.Model.*;
import org.example.Repository.IRepo;
import org.example.Service.TKD_Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * A controller class that handles the business logic for the TKD-Management system.
 */
public class TKDController {
    private TKD_Service tkdService;

    public TKD_Service getTkdService() {
        return tkdService;
    }

    public void setTkdService(TKD_Service tkdService) {
        this.tkdService = tkdService;
    }

    /**
     * Constructs a new TKDController with the given TKDService.
     *
     * @param tkdService The service that provides the business logic for the TKD-Management system.
     */
    public TKDController(TKD_Service tkdService) {
        this.tkdService = tkdService;
    }

    /**
     * Adds an object to repo.
     * @param obj           The object to be added.
     */
    public void addObject(Object obj) {
        try {
            tkdService.addObject(obj);
            if (obj instanceof Student) {
                System.out.println("Student" + ((Student) obj).getName() + " added with ID " + ((Student) obj).getId());
            } else if (obj instanceof Trainer) {
                System.out.println("Trainer" + ((Trainer) obj).getName() + " added with ID " + ((Trainer) obj).getId());
            } else if (obj instanceof Parent) {
                System.out.println("Parent" + ((Parent) obj).getName() + " added with ID " + ((Parent) obj).getId());
            } else if (obj instanceof Session) {
                System.out.println("Session with ID " + ((Session) obj).id + " added with ID " + ((Session) obj).getId());
            } else if (obj instanceof BeltExam) {
                System.out.println("Belt Exam with ID " + ((BeltExam) obj).getId() + " added");
            } else if (obj instanceof Contest) {
                System.out.println("Contest with ID " + ((Contest) obj).getId() + " added");
            } else if (obj instanceof TrainingCamp) {
                System.out.println("Training camp with ID " + ((TrainingCamp) obj).getId() + " added");
            }
        }catch (DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Deletes a student from the repo.
     * @param idStudent         The unique identifier of the student.
     */
    public void deleteStudent(Integer idStudent){
        try {
            tkdService.removeStudent(idStudent);
        } catch (EntityNotFoundException | DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
        System.out.println("Student with ID " + idStudent + " deleted");
    }

    /**
     * Deletes a trainer from the repo.
     * @param idTrainer         The unique identifier of the trainer.
     */
    public void deleteTrainer(Integer idTrainer){
        try {
            tkdService.removeTrainer(idTrainer);
        } catch (EntityNotFoundException | DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
        System.out.println("Trainer with ID " + idTrainer + " deleted");
    }

    /**
     * Deletes a parent from the repo.
     * @param idParent          The unique identifier of the parent.
     */
    public void deleteParent(Integer idParent) {
        try {
            tkdService.removeParent(idParent);
            System.out.println("Parent with ID " + idParent + " deleted");
        }catch (EntityNotFoundException | DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Deletes a session from the repo.
     * @param idSession         The unique identifier of the session.
     */
    public void deleteSession(Integer idSession) {
        try {
            tkdService.removeSession(idSession);
            System.out.println("Session with ID " + idSession + " deleted");
        }catch (EntityNotFoundException | DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Deletes a belt exam from the repo.
     * @param idBeltExam        The unique identifier of the belt exam.
     */
    public void deleteBeltExam(Integer idBeltExam) {
        try {
            tkdService.removeBeltExam(idBeltExam);
            System.out.println("Belt Exam with ID " + idBeltExam + " deleted");
        }catch (EntityNotFoundException | DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Deletes a contest from the repo.
     * @param idContest         The unique identifier of the contest.
     */
    public void deleteContest(Integer idContest) {
        try {
            tkdService.removeContest(idContest);
            System.out.println("Contest with ID " + idContest + " deleted");
        }catch (EntityNotFoundException | DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Deletes a training camp from the repo.
     * @param idTrainingCamp    The unique identifier of the training camp.
     */
    public void deleteTrainingCamp(Integer idTrainingCamp) {
        try {
            tkdService.removeTrainingCamp(idTrainingCamp);
            System.out.println("Training camp with ID " + idTrainingCamp + " deleted");
        }catch (EntityNotFoundException | DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Changes the session of the trainer
     * @param idTrainer     The unique identifier of the trainer.
     * @param idSession     The unique identifier of the session.
     */
    public void assignSessionToTrainer(Integer idSession, Integer idTrainer) {
        try {
            tkdService.assignGroupToTrainer(idTrainer, idSession);
            System.out.println("Trainer with ID " + idTrainer + " assigned to session with ID " + idSession);
        }catch (EntityNotFoundException | DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Changes the session of the student.
     * @param idStudent The unique identifier of the student.
     * @param idSession The unique identifier of the session.
     */
    public void changeStudentSession(Integer idStudent, Integer idSession) {
        try {
            tkdService.changeStudentGroup(idStudent, idSession);
            System.out.println("Student with ID " + idStudent + " changed to session with ID " + idSession);
        }catch (EntityNotFoundException | DatabaseException | BusinessLogicException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Counts the number of attendances and absences of a student.
     * @param idStudent     The unique identifier of the student.
     */
    public void numberOfAttendances(Integer idStudent) {
        try {
            Map<String, Integer> attendencesAndAbsences = tkdService.numberOfAttendencesAndAbsences(idStudent);
            System.out.println("Student with id " + idStudent + " has " + attendencesAndAbsences.get("Attendences") + " attendances " +
                    attendencesAndAbsences.get("Absences") + " absences");
        }catch (EntityNotFoundException | DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Adds a student to a belt exam.
     * @param idBeltExam    The unique identifier of the belt exam.
     * @param idStudent     The unique identifier of the student.
     */
        public void addStudentToBeltExam(Integer idBeltExam, Integer idStudent) {
        try {
            tkdService.addStudentToBeltExam(idStudent, idBeltExam);
            System.out.println("Student with id " + idStudent + " added to Belt Exam with ID " + idBeltExam);
        }catch (EntityNotFoundException | DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Adds a result to a belt exam.
     * @param idBeltExam    The unique identifier of the belt exam.
     * @param idStudent     The unique identifier of the student.
     * @param result        The result which is true(passed) or false(failed).
     */
    public void addResultToBeltExam(Integer idBeltExam, Integer idStudent, Boolean result) {
        try {
            tkdService.addResultBeltExam(idStudent, idBeltExam, result);
            if (result) {
                System.out.println("Student with id " + idStudent + " has promoted Belt Exam with ID " + idBeltExam);
            } else {
                System.out.println("Student with id " + idStudent + " has not promoted Belt Exam with ID " + idBeltExam);
            }
        }catch (EntityNotFoundException | DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Adds an attendance to a student.
     * @param idStudent     The unique identifier of the student.
     * @param idSession     The unique identifier of the belt exam.
     * @param attendance    The attendance which can be true(present) or false(absent).
     * @param weekDay       The day of the week of the attendance.
     * @param date          The date of the session.
     */
    public void addAttendance(Integer idStudent,Integer idSession, Boolean attendance, String weekDay, String date){
        try {
            tkdService.addAttendance(idStudent, idSession, attendance, weekDay, date);
            if (attendance) {
                System.out.println("Student with id " + idStudent + " has attended the session with ID " + idSession + " on " + weekDay + " from " + date);
            } else {
                System.out.println("Student with id " + idStudent + " has not attended the session with ID " + idSession + " on " + weekDay + " from " + date);
            }
        }catch (DatabaseException | EntityNotFoundException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Adds a student to a contest.
     * @param idStudent     The unique identifier of the student.
     * @param idContest     The unique identifier of the contest.
     */
    public void addStudentToContest(Integer idStudent, Integer idContest) {
        try {
            tkdService.addStudentToContest(idStudent, idContest);
            System.out.println("Student with id " + idStudent + " has been added to contest with id " + idContest);
        }catch (EntityNotFoundException | DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Adds a student to a training camp.
     * @param idStudent         The unique identifier of the student.
     * @param idTrainingCamp    The unique identifier of the training camp.
     */
    public void addStudentToTrainingCamp(Integer idStudent, Integer idTrainingCamp) {
        try {
            tkdService.addStudentToTraining(idStudent, idTrainingCamp);
            System.out.println("Student with id " + idStudent + " has been added to training camp with id " + idTrainingCamp);
        }catch (EntityNotFoundException | DatabaseException | BusinessLogicException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Displays all students from the session.
     */
    public void viewStudents(){
        try{
            System.out.println("Here are all students: \n" + tkdService.viewAllStudents());
        }catch (DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
//        System.out.println();
    }

    /**
     * Displays all trainers from the session.
     */
    public void viewTrainers(){
        try {
            System.out.println("Here are all trainers: ");
            System.out.println(tkdService.viewAllTrainers());
        }catch (DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Displays all contests.
     */
    public void viewContests(){
        try {
            System.out.println("Here are all contests: ");
            System.out.println(tkdService.viewAllContests());
        }catch (DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Displays all training camps.
     */
    public void viewTrainingCamps() {
        try {
            System.out.println("Here are all training camps: ");
            System.out.println(tkdService.viewTrainingCamps());
        }catch (DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Displays all belt exams.
     */
    public void viewBeltExams() {
        try {
            System.out.println("Here are all belt exams: ");
            System.out.println(tkdService.viewBeltExams());
        }catch (DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Displays all parents.
     */
    public void viewParents() {
        try {
            System.out.println("Here are parents with id, name and their children: ");
            System.out.println(tkdService.viewAllParents());
        }catch (DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Adds a student to a parent.
     * @param student   The student to be added.
     * @param parent    The parent to be added.
     */
    public void addStudentToParent(Student student, Parent parent){
        try {
            tkdService.addStudentToParent(student, parent);
        }
        catch (DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Generates an invoice for a month for a parent for all its children.
     * @param parentID          The unique identifier of a parent.
     * @param month             The month for the invoice.
     */
    public void generateInvoice(Integer parentID,String month){
        try {
            System.out.println(tkdService.generateInvoice(parentID, month));
        }catch (DatabaseException | EntityNotFoundException | BusinessLogicException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Adds a student to a session.
     * @param idStudent   The unique identifier of a student.
     * @param idSession   The unique identifier of a session.
     */
    public void addStudentToSession(Integer idSession, Integer idStudent) {
        try {
            tkdService.addStudentToSession(idSession, idStudent);
            System.out.println("Student with id " + idStudent + " has been added to session with id " + idSession);
        }catch (EntityNotFoundException | DatabaseException | BusinessLogicException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * Gets a trainer by id.
     * @param idTrainer         The unique identifier of a trainer.
     * @return                  The trainer.
     */
    public Trainer getTrainerById(Integer idTrainer){
        try {
            return tkdService.getTrainerById(idTrainer);
        }catch(EntityNotFoundException | DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
        return null;
    }

    /**
     * Gets a trainer by id.
     * @param idSession         The unique identifier of a session.
     * @return                  The session.
     */
    public Session getSessionById(Integer idSession){
        try {
            return tkdService.getSessionById(idSession);
        }catch(EntityNotFoundException | DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
        return null;
    }

    /**
     * Shows every combination of events that a student can attend to for a given amount of money.
     * @param amountOfMoney The maximum value of a combination.
     */
    public void eventsThatdontExceedAmountOfMoney(double amountOfMoney) {
        try {
            List<List<Integer>> results = null;
            try {
                results = tkdService.eventsThatdontExceedAmountOfMoney(amountOfMoney);
            } catch (DatabaseException | EntityNotFoundException | BusinessLogicException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Events combinations that don t exceed the sum : " + amountOfMoney);
            for (List<Integer> combination : results) {
                System.out.println("ID-urile evenimentelor: ");
                for (Integer c : combination) {
                    if (c > 100 && c < 200) { // id for Contests
                        Contest cnt = tkdService.getContestById(c);
                        System.out.println(cnt.toString2());
                    } else if (c > 10 && c < 100) {
                        TrainingCamp tc = tkdService.getTrainingCampByIs(c);
                        System.out.println(tc.toString2());
                    }
                }
                System.out.println('\n');
            }
        }catch (DatabaseException | EntityNotFoundException e){
            System.out.println("Error: " + e.getMessage());
        }
    }


    /**
     * prints a sorted list of contests based on their start date
     */
    public void sortedContests(){
        try {
            for (Contest c1 : tkdService.sortContestsByDates()) {
                System.out.println(c1.toString2());
                System.out.println('\n');
            }
        }catch(DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * prints a sorted list of trainingCamps based on their starting date
     */
    public void sortedCampsByDates(){

        try{
            for(TrainingCamp tc: tkdService.sortTrainingCampsByDates()){
                System.out.println(tc.toString2());
                System.out.println('\n');
            }
        }catch (DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * prints a sorted list of BeltExamns based on their starting dates
     */
    public void sortBeltExamnsByDates(){
        try{
            for(BeltExam bt: tkdService.sortBeltExamnsByDates()){
                System.out.println(bt.toString2());
                System.out.println('\n');
            }
        }catch(DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * prints a sorted list of students based on their number of attendances
     */
    public void sortedStudentsByAttend(){
        try{
            for(Student s1 : tkdService.sortStudentsByNumberOfAttendences()){
                System.out.println(s1.toString2());
                System.out.println('\n');
            }
        }catch(EntityNotFoundException |DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * prints a filtered list containing just students with the specified belt level
     * @param beltLevel The belt level that is used for filtering the students
     */
    public void filteredStudentsByBeltLevel(BeltLevel beltLevel) {
        try{
            for(Student s1 : tkdService.filterStudentsByBelt(beltLevel)){
                System.out.println(s1.toString2());
                System.out.println('\n');
            }
        }
        catch(DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }


    /**
     * prints a filtred list of parents that have a specific number of children
     * @param noOfChildren      number of children
     */
    public void filterParentsByNumberOfChildren(Integer noOfChildren) {
        try {
            if(tkdService.filterParentsNumberOfChildren(noOfChildren).size() == 0){
                System.out.println("No parent with " + noOfChildren + " children");
            }
        } catch (DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
        try {
            for(Parent p1: tkdService.filterParentsNumberOfChildren(noOfChildren)){
                System.out.println(p1.toString2());
                System.out.println('\n');
            }
        } catch (DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * prints a list of students ordered alphabetical
     */
    public void sortStudentsAlphabetical() {
        try {
            List<Student> students = tkdService.sortStudentsAlphabetical();
            for (Student s : students) {
                System.out.println(s.toString2());
            }
        }catch(DatabaseException e) {
            System.out.println("Error: "+ e.getMessage());
        }
    }

    /**
     * prints a list of sessions ordered by number of participants
     */
    public void sortSessionByNumberOfParticipants() {
        try {
            List<Session> sessions = tkdService.sortSessionByNumberOfParticipants();
            for (Session s : sessions) {
                System.out.println(s.toString2());
            }
        }catch(DatabaseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Gets for a given session the date with the biggest profit made and the amount made and prints the outputs.
     * @param sessionId         The unique identifier of the session.
     */
    public void getMostProfitableDateForSession(int sessionId) {
        try {
            String date = tkdService.getMostProfitableDateForSession(sessionId).getKey();
            double max = tkdService.getMostProfitableDateForSession(sessionId).getValue();
            System.out.println("The date with most students at the session with id " + sessionId + " is " + date + " and made " + max + " RON.");
        }catch (DatabaseException | EntityNotFoundException | BusinessLogicException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Gets the first free student id
     * @return  First free student id
     */
    public int getStudentId(){
        int id = 0;
        try {
            id = tkdService.getStudentId();
        } catch (DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return id;
    }

    /**
     * Gets the first free session id
     * @return  First free session id
     */
    public int getSessionId(){
        int id = 0;
        try {
            id = tkdService.getSessionId();
        } catch (DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return id;
    }

    /**
     * Gets the first free trainer id
     * @return  First free trainer id
     */
    public int getTrainerId(){
        int id = 0;
        try {
            id = tkdService.getTrainerId();
        } catch (DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return id;
    }

    /**
     * Gets the first free parent id
     * @return  First free parent id
     */
    public int getParentId(){
        int id = 0;
        try {
            id = tkdService.getParentId();
        } catch (DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return id;
    }

    /**
     * Gets the first free contest id
     * @return  First free contest id
     */
    public int getContestId(){
        int id = 0;
        try {
            id = tkdService.getContestId();
        } catch (DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return id;
    }

    /**
     * Gets the first free training camp id
     * @return  First free training camp id
     */
    public int getTrainingCampId(){
        int id = 0;
        try {
            id = tkdService.getTrainingCampId();
        } catch (DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return id;
    }

    /**
     * Gets the first free belt exam id
     * @return  First free belt exam id
     */
    public int getBeltExamId(){
        int id = 0;
        try {
            id = tkdService.getBeltExamId();
        } catch (DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return id;
    }

}
