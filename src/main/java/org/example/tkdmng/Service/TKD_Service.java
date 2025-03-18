package org.example.Service;

import org.example.Exceptions.BusinessLogicException;
import org.example.Exceptions.DatabaseException;
import org.example.Exceptions.EntityNotFoundException;
import org.example.Model.*;
import org.example.Repository.DatabaseRepo;
import org.example.Repository.IRepo;
import org.example.Repository.InMemoryRepo;
import org.example.Repository.InMemoryRepo;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A service class that provides the business logic for the TKD-Management system.
 */
public class TKD_Service {

    private IRepo<Student> students;

    private IRepo<Trainer> trainers;

    private IRepo<Parent> parents;

    private IRepo<Session> sessions;

    private IRepo<Contest> contests;

    private IRepo<TrainingCamp> trainingCamps;

    private IRepo<BeltExam> beltExams;

    /**
     * Constructs a new TKD_Service with the given repositories.
     * @param students          The repository for students.
     * @param trainers          The repository for trainers.
     * @param parent            The repository for parents.
     * @param sessions          The repository for sessions.
     * @param contests          The repository for contests.
     * @param trainingCamps     The repository for training camps.
     * @param beltExams         The repository for belt exams.
     */
    public TKD_Service(IRepo<Student> students, IRepo<Trainer> trainers, IRepo<Parent> parent, IRepo<Session> sessions, IRepo<Contest> contests, IRepo<TrainingCamp> trainingCamps, IRepo<BeltExam> beltExams) {
        this.students = students;
        this.trainers = trainers;
        this.parents = parent;
        this.sessions = sessions;
        this.contests = contests;
        this.trainingCamps = trainingCamps;
        this.beltExams = beltExams;
    }


    /**
     * Change the trainer of a session.
     * @param trainerId     The unique identifier of a trainer.
     * @param sessionId     The unique identifier of a session.
     * @throws EntityNotFoundException or DataBaseException   If no trainer or session was found.
     */
    public void assignGroupToTrainer(int trainerId, int sessionId) throws EntityNotFoundException, DatabaseException {
        Trainer tr = null;
        try {
            tr = trainers.get(trainerId);
            if(tr == null){
                throw new EntityNotFoundException("No trainer with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        Session ss = null;
        try {
            ss = sessions.get(sessionId);
            if(ss == null){
                throw new EntityNotFoundException("No session with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        ss.trainer=tr.getId();
        try {
            sessions.update(ss);
        } catch (DatabaseException e) {
            throw e;
        }

    }

    /**
     * Change the session of a student and remove him from the previous one.
     * @param studentId     The unique identifier of a student.
     * @param sessionId     The unique identifier of a session.
     * @throws EntityNotFoundException or DataBaseException  If no student or session was found.
     */
    public void changeStudentGroup(int studentId,int sessionId) throws EntityNotFoundException, DatabaseException, BusinessLogicException {
        Student st = null;
        try {
            st = students.get(studentId);
            if(st == null){
                throw new EntityNotFoundException("No student with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        Session new_ss = null;
        try {
            new_ss = sessions.get(sessionId);
            if(new_ss == null){
                throw new EntityNotFoundException("No new session with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        if(new_ss.getSessionStudents().size()+1==new_ss.getMaximumParticipants()){
            throw new BusinessLogicException("The session is already full");
        }
        Session old_ss = null;
        try {
            old_ss = sessions.get(st.getSession());
            if(old_ss == null){
                throw new EntityNotFoundException("No old session with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }

        old_ss.getSessionStudents().remove(st.getId());
        new_ss.getSessionStudents().add(st.getId());
        st.setSession(new_ss.id);
        try {
            students.update(st);
        } catch (DatabaseException e) {
            throw e;
        }
        try {
            sessions.update(new_ss);
        } catch (DatabaseException e) {
            throw e;
        }
        try {
            sessions.update(old_ss);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * Changes the belt color of a student if he passed the exam.
     * @param beltExamID  The unique identifier of a belt exam.
     * @throws EntityNotFoundException or DataBaseException    If no student or belt exam was found.
     */
    public void changeBeltlevel(Integer beltExamID,Integer studentID) throws EntityNotFoundException, DatabaseException {
        try {
            BeltExam beltExam = beltExams.get(beltExamID);
            if(beltExam == null){
                throw new EntityNotFoundException("No belt exam with this ID found");
            }
            Student st = students.get(studentID);
            st.setBeltLevel(beltExam.getBeltColor());
            students.update(st);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * Counts the number of attendances and absences of a student.
     * @param studentId         The unique identifier of a student.
     * @return                  Map containing the number of attendances and absences of a student.
     * @throws EntityNotFoundException or DatabaseException     If no student was found.
     *
     */
    public Map<String,Integer> numberOfAttendencesAndAbsences(int studentId) throws EntityNotFoundException, DatabaseException {
        Student st= null;
        try {
            st = students.get(studentId);
            if(st == null){
                throw new EntityNotFoundException("No student with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }

        Map<String,Integer> attendencesAbsences= new HashMap<>();
        attendencesAbsences.put("Attendences",0);
        attendencesAbsences.put("Absences",0);

        for(SessionDate sd: st.getSessionDateList()){
            if(sd.isAttended()){
                int count = attendencesAbsences.get("Attendences");

                attendencesAbsences.put("Attendences",count+1);
            }
            else{
                int count = attendencesAbsences.get("Absences");
                attendencesAbsences.put("Absences",count+1);
            }
        }
        return attendencesAbsences;
    }

    /**
     * this is a backtrcking function that find evry possible combination between Contests and TrainingCamps
     * @param eventPairs            a list with information that holds all Contest/TrainingCamp and their prices
     * @param remainingAmount       the amount of money after one Contest/TrainingCamp has been chosen
     * @param start                 starting point where the function start to search
     * @param currentCombination    the combination on every search
     * @param results               this is a parameter where all combinations will be found
     */
    private void findCombinations(List<Map.Entry<Integer, Double>> eventPairs, double remainingAmount,int start, List<Integer> currentCombination, List<List<Integer>> results) {
        if (remainingAmount == 0) {
            results.add(new ArrayList<>(currentCombination));
            return;
        }

        for (int i = start; i < eventPairs.size(); i++) {
            Map.Entry<Integer, Double> event = eventPairs.get(i);
            if (event.getValue() > remainingAmount) continue;

            currentCombination.add(event.getKey());
            findCombinations(eventPairs, remainingAmount - event.getValue(), i, currentCombination, results);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }


    /**
     * it takes the amount of money given by controller and returns a list with every combination
     * @param amountOfMoney     the range that it's forbidden to be exceeded
     * @return                  a list of lists with all possible combinations
     * @throws BusinessLogicException       If no event with max the amount of money was found
     * @throws EntityNotFoundException or DatabaseException     If no contest or training camp was found.
     */
    public List<List<Integer>> eventsThatdontExceedAmountOfMoney(double amountOfMoney) throws DatabaseException, EntityNotFoundException, BusinessLogicException {
        try{
            List<Map.Entry<Integer, Double>> eventPairs = new ArrayList<>();
            boolean flag1 = false;
            for (int i = 0; i < contests.getAll().size(); i++) {
                Contest ct = contests.getAll().get(i);
                if(ct.price <= amountOfMoney){
                    flag1 = true;
                }
                if(ct == null){
                    throw new EntityNotFoundException("No contest with this ID found");
                }
                eventPairs.add(new AbstractMap.SimpleEntry<>(ct.getId(), ct.price));
            }

            for (int i = 0; i < trainingCamps.getAll().size(); i++) {
                TrainingCamp ct = trainingCamps.getAll().get(i);
                if(ct.price <= amountOfMoney){
                    flag1 = true;
                }
                if(ct == null){
                    throw new EntityNotFoundException("No training camp with this ID found");
                }
                eventPairs.add(new AbstractMap.SimpleEntry<>(ct.getId(), ct.price));
            }
            if(flag1 == true) {
                List<List<Integer>> results = new ArrayList<>();
                findCombinations(eventPairs, amountOfMoney, 0, new ArrayList<>(), results);
                return results;
            }else{
                throw new BusinessLogicException("No event that has at least " + amountOfMoney + " money was found");
            }

        }catch (DatabaseException e){
            throw e;
        }

    }

    /**
     * Adds a student to a belt exam.
     * @param idStudent     The unique identifier of a student.
     * @param idBeltExam    The unique identifier of a belt exam.
     * @throws EntityNotFoundException or DatabaseException  If no student or belt exam was found.
     */
    public void addStudentToBeltExam(int idStudent, int idBeltExam) throws EntityNotFoundException, DatabaseException {
        Student s = null;
        try {
            s = students.get(idStudent);
            if(s == null){
                throw new EntityNotFoundException("No student with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        BeltExam belt = null;
        try {
            belt = beltExams.get(idBeltExam);
            if(belt == null){
                throw new EntityNotFoundException("No belt exam with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        belt.getListOfResults().put(s.getId(),-1);
        try {
            beltExams.update(belt);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * Updates the result of a belt exam of a student and if promoted changes the belt color.
     * @param idStudent     The unique identifier of a student.
     * @param idBeltExam    The unique identifier of a belt exam.
     * @param promoted      The result of the exam, passed(true) or failed(false).
     * @throws EntityNotFoundException or DatabaseException  If no student or belt exam was found.
     */
    public void addResultBeltExam(int idStudent, int idBeltExam, boolean promoted) throws EntityNotFoundException, DatabaseException {
        Student s = null;
        try {
            s = students.get(idStudent);
            if(s == null){
                throw new EntityNotFoundException("No student with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        BeltExam belt = null;
        try {
            belt = beltExams.get(idBeltExam);
            if(belt == null){
                throw new EntityNotFoundException("No belt exam with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        if(promoted){
            belt.getListOfResults().put(s.getId(),1); // promoted
            changeBeltlevel(idBeltExam,idStudent);
        }
        else{
            belt.getListOfResults().put(s.getId(),0); // failed
        }
        try {
            beltExams.update(belt);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * Adds an attendance/absence to the list of session dates from the student.
     * @param studentId     The unique identifier of a student.
     * @param sessionId     The unique identifier of a session.
     * @param attendance    The attendance of a student which can be true(was present) or false(wasn't present).
     * @param weekday       The day of the week of the session.
     * @param date          The exact date the session took place.
     * @throws EntityNotFoundException or DatabaseException  If no student or session was found.
     */
    public void addAttendance(int studentId,int sessionId,boolean attendance,String weekday,String date) throws EntityNotFoundException, DatabaseException {
        Student s = null;
        try {
            s = students.get(studentId);
            if(s == null){
                throw new EntityNotFoundException("No student with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        SessionDate sessionDate = new SessionDate(weekday,date,sessionId,attendance);
            s.getSessionDateList().add(sessionDate);
            try {
                students.update(s);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * Adds a student to a contest.
     * @param studentId     The unique identifier of a student.
     * @param contestId     The unique identifier of a contest.
     * @throws EntityNotFoundException or DatabaseException  If no student or contest was found.
     */
    public void addStudentToContest(int studentId,int contestId) throws EntityNotFoundException, DatabaseException {
        Student st = null;
        try {
            st = students.get(studentId);
            if(st == null){
                throw new EntityNotFoundException("No student with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        Contest ct = null;
        try {
            ct = contests.get(contestId);
            if(ct == null){
                throw new EntityNotFoundException("No contest with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        ct.getStudents().add(st.getId());
        st.getContestList().add(ct.getId());
        try {
            contests.update(ct);
        } catch (DatabaseException e) {
            throw e;
        }
        try {
            students.update(st);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * Adds a student to a training camp.
     * @param studentId         The unique identifier of a student.
     * @param trainingCampId    The unique identifier of a training camp.
     * @throws EntityNotFoundException or DatabaseException      If no student or training camp was found.
     */
    public void addStudentToTraining(int studentId,int trainingCampId) throws EntityNotFoundException, DatabaseException, BusinessLogicException {
        Student st = null;
        try {
            st = students.get(studentId);
            if(st == null){
                throw new EntityNotFoundException("No student with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        TrainingCamp tc = null;
        try {
            tc = trainingCamps.get(trainingCampId);
            if(tc == null){
                throw new EntityNotFoundException("No training camp with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        if(tc.getStudents().size() + 1 == tc.getNumberOfParticipants()){
            throw new BusinessLogicException("The training camp is already full");
        }
        tc.getStudents().add(st.getId());
        st.getTrainingCampList().add(tc.getId());

        try {
            trainingCamps.update(tc);
        } catch (DatabaseException e) {
            throw e;
        }
        try {
            students.update(st);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * Adds a student to a parent, by searching the for the parent by email to see if it needs to be added to the repo.
     * @param student   The student object that needs to be added to the parent.
     * @param parent    The parent object that needs to be updated/ created.
     * @throws  DatabaseException   If error with database.
     */
    public void addStudentToParent(Student student, Parent parent) throws DatabaseException {
        if(findParent(parent.getEmail())){
            Parent updateParent = null;
            try {
                updateParent = parents.getAll().stream().filter(pt -> Objects.equals(pt.getEmail(),parent.getEmail())).findFirst().orElse(null);
            } catch (DatabaseException e) {
                throw e;
            }
            updateParent.getChildren().add(student.getId());
            try {
                parents.update(updateParent);
            } catch (DatabaseException e) {
                throw e;
            }
            student.setParent(updateParent.getId());
        }
        else {
            try {
                parents.add(parent);
            } catch (DatabaseException e) {
                throw e;
            }
            parent.getChildren().add(student.getId());
            try {
                parents.update(parent);
            } catch (DatabaseException e) {
                throw e;
            }
            student.setParent(parent.getId());
        }
        students.update(student);
    }

    /**
     * Searches in the parents repo for a parent by email and return true, if he exists.
     * @param email     the unique email of a Parent
     * @return          true/false if parent found
     * @throws DatabaseException    If error with database.
     */
    public boolean findParent(String email) throws DatabaseException {
        try {
            return parents.getAll().stream().anyMatch(pt -> Objects.equals(pt.getEmail(), email));
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * this function adds an object in their repo based on their type
     * @param  o                   represent the given object
     * @throws DatabaseException   If object id already exists.
     */
    public void addObject(Object o) throws DatabaseException {
        if(o instanceof Student){
            try {
                students.add((Student) o);
            } catch (DatabaseException e) {
                throw e;
            }

        }
        else if(o instanceof Trainer){
            try {
                trainers.add((Trainer) o);
            } catch (DatabaseException e) {
                throw e;
            }
        }
        else if(o instanceof Parent){
            try {
                parents.add((Parent) o);
            } catch (DatabaseException e) {
                throw e;
            }
        }
        else if(o instanceof Session){
            try {
                sessions.add((Session) o);
            } catch (DatabaseException e) {
                throw e;
            }
        }
        else if(o instanceof BeltExam){
            try {
                beltExams.add((BeltExam) o);
            } catch (DatabaseException e) {
                throw e;
            }
        }
        else if(o instanceof Contest){
            try {
                contests.add((Contest) o);
            } catch (DatabaseException e) {
                throw e;
            }
        }
        else if(o instanceof TrainingCamp){
            try {
                trainingCamps.add((TrainingCamp) o);
            } catch (DatabaseException e) {
                throw e;
            }
        }
    }


    /**
     * make an Invoice for every parent based on the month
     * @param parentID          the ID of the parent
     * @param month             the month they want an invoice
     * @return                  a string that holds information an invoice need to have (for every child they have)
     * @throws EntityNotFoundException or DatabaseException      If no parent was found.
     */

    public String generateInvoice(Integer parentID,String month) throws DatabaseException, EntityNotFoundException, BusinessLogicException {
        Parent parent = null;
        try {
            parent = parents.get(parentID);
            if(parent == null){
                throw new EntityNotFoundException("No parent with this ID found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        String invoice="Invoice for the month " + month + "\nParent name: " + parent.getLastName() + " " + parent.getName() + "\n";
        double total = 0;
        for(int studentId: parent.getChildren()){
            Student student = null;
            try {
                student = students.get(studentId);
                if(student == null){
                    throw new EntityNotFoundException("No student with this ID found");
                }
            } catch (DatabaseException e) {
                throw e;
            }
            int presences = 0;
            double individualTotal = 0;
            for(SessionDate sd: student.getSessionDateList()){
                if(sd.isAttended() && sd.getDate().substring(5, 7).equals(month)){
                    presences++;
                }
            }
            try {
                individualTotal += presences*(sessions.get(student.getSession())).getPricePerSession();
            } catch (DatabaseException e) {
                throw e;
            }
            total+= individualTotal;
            invoice += "Student name: " + student.getLastName() + " " + student.getName() + "\n Total for student: " + individualTotal + "\n";
        }
        if(total == 0){
            throw new BusinessLogicException("None of the children/s of the parent has been present to class in the " + month);
        }
        invoice += "Total: " + total +"\n";
        return invoice;
    }

    /**
     * it deletes a student based on their ID
     * @param studentID         the id of the student
     * @throws EntityNotFoundException or DatabaseException      If no student was found.
     */
    public void removeStudent(Integer studentID) throws EntityNotFoundException, DatabaseException {
        try {
            if(students.getAll().stream().noneMatch(st -> st.getId() == studentID)){
                throw new EntityNotFoundException("Invalid student ID");
        }

    } catch (DatabaseException e) {
        throw e;
    }
        Parent parent = null;
        try {
            parent = parents.get(students.get(studentID).getParent());
            if(parent == null){
                throw new EntityNotFoundException("No parent with this ID-Student found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        if(parent.getChildren().size()>1){
            parent.getChildren().remove(studentID);
            try {
    parents.update(parent);
    } catch (DatabaseException e) {
        throw e;
    }
        }
        else{
            try {
                parents.remove(parent.getId());
            } catch (DatabaseException e) {
                throw e;
            }
        }
        Session session = null;
        try {
            session = sessions.get(students.get(studentID).getSession());
            if(session == null){
                throw new EntityNotFoundException("No session assign to these ID student found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        session.getSessionStudents().remove( studentID);
        try {
        sessions.update(session);

        } catch (DatabaseException e) {
            throw e;
        }
        try {
            students.remove(studentID);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * it deletes a trainer based on their ID
     * @param trainerID         the id of the trainer
     * @throws EntityNotFoundException or DatabaseException      If no trainer was found.
     */
    public void removeTrainer(Integer trainerID) throws EntityNotFoundException, DatabaseException {
        try {
            if(trainers.getAll().stream().noneMatch(t -> t.getId() == trainerID)){
                throw new EntityNotFoundException("Invalid trainer ID");
        }

        } catch (DatabaseException e) {
            throw e;
        }
        try {
            trainers.remove(trainerID);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * it deletes a parent based on their ID
     * @param parentID          the id of the Parent
     * @throws EntityNotFoundException or DatabaseException     If no parent was found.
     */
    public void removeParent(Integer parentID) throws EntityNotFoundException, DatabaseException {
        try {
            if(parents.getAll().stream().noneMatch(pt -> pt.getId() == parentID)){
                    throw new EntityNotFoundException("Invalid parent ID");
            }

        } catch (DatabaseException e) {
            throw e;
        }
        try {
            List<Integer> children = parents.get(parentID).getChildren();
            for(int i=0; i<children.size(); i++){
                Session session = null;
                try {
                    session = sessions.get(students.get(children.get(i)).getSession());
                    if(session == null){
                        throw new EntityNotFoundException("No session assign to these ID student found");
                    }
                } catch (DatabaseException e) {
                    throw e;
                }
                session.getSessionStudents().remove(children.get(i));
                try {
                    sessions.update(session);

                } catch (DatabaseException e) {
                    throw e;
                }
                try {
                    students.remove(children.get(i));
                } catch (DatabaseException e) {
                    throw e;
                }
            }
            parents.remove(parentID);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * it deletes a session based on their ID
     * @param sessionID         the id of the session
     * @throws EntityNotFoundException or DatabaseException      If no session was found.
     */
    public void removeSession(Integer sessionID) throws EntityNotFoundException, DatabaseException {
        try {
        if(sessions.getAll().stream().noneMatch(ss -> ss.getId() == sessionID)){
                throw new EntityNotFoundException("Invalid session ID");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        try {
            for (int i = 0; i < sessions.get(sessionID).getSessionStudents().size(); i++) {
                Student student = students.get(sessions.get(sessionID).getSessionStudents().get(i));
                student.setSession(0);
                students.update(student);
            }
        }
        catch (DatabaseException e) {
            throw e;
        }
        try {
            sessions.remove(sessionID);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * it deletes a BeltExam based on their ID
     * @param beltExamID        the id of the BeltExam
     * @throws EntityNotFoundException or DatabaseException      If no belt exam was found.
     */
    public void removeBeltExam(Integer beltExamID) throws EntityNotFoundException, DatabaseException {
        try {
    if(beltExams.getAll().stream().noneMatch(bt -> bt.getId() == beltExamID)){
            throw new EntityNotFoundException("Invalid belt exam ID");
        }

        } catch (DatabaseException e) {
            throw e;
        }
        try {
            beltExams.remove(beltExamID);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * it deletes a Contest based on their ID
     * @param contestID         the id of the contest
     * @throws EntityNotFoundException or DatabaseException      If no contest was found.
     */
    public void removeContest(Integer contestID) throws EntityNotFoundException, DatabaseException {
        try {
            if(contests.getAll().stream().noneMatch(ct -> ct.getId() == contestID)){
                throw new EntityNotFoundException("Invalid contest ID");
        }

        } catch (DatabaseException e) {
            throw e;
        }
        try {
            contests.remove(contestID);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * it deletes a training camp based on their ID
     * @param trainingCampID    the id of the trainingCamp
     * @throws EntityNotFoundException or DatabaseException      If no training camp was found.
     */
    public void removeTrainingCamp(Integer trainingCampID) throws EntityNotFoundException, DatabaseException {
        try {
            if(trainingCamps.getAll().stream().noneMatch(tc -> tc.getId() == trainingCampID)){
                    throw new EntityNotFoundException("Invalid training camp ID");
                }

        } catch (DatabaseException e) {
            throw e;
        }
        try {
            trainingCamps.remove(trainingCampID);
        } catch (DatabaseException e) {
            throw e;
        }
    }


    /**
     * assign a student to a session
     * @param idSession the id of the session
     * @param studentID the id of the Student
     * @throws EntityNotFoundException or DatabaseException     If no session or student was found.
     */
    public void addStudentToSession(Integer idSession, Integer studentID) throws EntityNotFoundException, DatabaseException, BusinessLogicException {

        Session ss = null;
        try {
            ss = sessions.get(idSession);
            if(ss == null){
                throw new EntityNotFoundException("No session with these id found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        if(ss.getSessionStudents().size() + 1 == ss.getMaximumParticipants()){
            throw new BusinessLogicException("Session is already full");
        }
        Student st = null;
        try {
            st = students.get(studentID);
            if(st == null){
                throw new EntityNotFoundException("No student with these id found");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        ss.getSessionStudents().add(st.getId());
        try {
            sessions.update(ss);
        } catch (DatabaseException e) {
            throw e;
        }

    }

    /**
     * get a Trainer based on their id
     * @param trainerId         the trainer id
     * @return                  object of type Trainer
     * @throws EntityNotFoundException or DatabaseException      If no trainer was found.
     */
    public Trainer getTrainerById(int trainerId) throws EntityNotFoundException, DatabaseException {
        try {
            if(trainers.getAll().stream().noneMatch(st -> st.getId() == trainerId)){
                throw new EntityNotFoundException("Invalid trainer ID");
        }

        } catch (DatabaseException e) {
            throw e;
        }
        try {
            Trainer tr = trainers.get(trainerId);
            if(tr == null){
                throw new EntityNotFoundException("No trainer with these id found");
            }
            return trainers.get(trainerId);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * get a Contest based on their id
     * @param       contestID the contest id
     * @return      an object of type Contest
     * @throws EntityNotFoundException or DatabaseException     If no contest was found.
     */
    public Contest getContestById(int contestID) throws EntityNotFoundException, DatabaseException {
        try {
            Contest c = contests.get(contestID);
            if(c == null){
                throw new EntityNotFoundException("No contest with these id found");
            }
            return contests.get(contestID);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * get a Training Camp based on their id
     * @param idTrainingCamp    the Training Camp id
     * @return                  an object of type Training Camp
     * @throws EntityNotFoundException or DatabaseException     If no training camp was found.
     */
    public TrainingCamp getTrainingCampByIs(int idTrainingCamp) throws EntityNotFoundException, DatabaseException {
        try {
            TrainingCamp tc = trainingCamps.get(idTrainingCamp);
            if(tc == null){
                throw new EntityNotFoundException("No training camp with these id found");
            }
            return trainingCamps.get(idTrainingCamp);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * get a session based on their id
     * @param sessionId         the session id
     * @return                  an object of type Session
     * @throws EntityNotFoundException or DatabaseException      If no session was found.
     */
    public Session getSessionById(int sessionId) throws EntityNotFoundException, DatabaseException {
        try {
            if(sessions.getAll().stream().noneMatch(st -> st.getId() == sessionId)){
                throw new EntityNotFoundException("Invalid session ID");
        }

        } catch (DatabaseException e) {
            throw e;
        }
        try {
            Session s = sessions.get(sessionId);
            if(s == null){
                throw new EntityNotFoundException("No session with these id found");
            }
            return sessions.get(sessionId);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /** display all Students
     *
     * @return a String that holds all students
     * @throws DatabaseException    If error in database.
     */
    public String viewAllStudents() throws DatabaseException {
        StringBuilder allStudents= new StringBuilder();
        try {
            for(Session s: sessions.getAll()){
                for(int stId: s.getSessionStudents()){
                    allStudents.append(students.get(stId).toString2());
                }
                allStudents.append('\n');
            }
        } catch (DatabaseException e) {
            throw e;
        }
        return allStudents.toString();
    }

    /**
     * display all Trainers
     * @return a String that holds all trainers
     * @throws DatabaseException    If error in database.
     */
    public String viewAllTrainers() throws DatabaseException {
        StringBuilder allTrainers= new StringBuilder();
        try {
            for(Trainer t: trainers.getAll()){
                allTrainers.append(t.toString2()).append('\n');
            }
        } catch (DatabaseException e) {
            throw e;
        }
        return allTrainers.toString();
    }

    /**
     * dispaly all Parents
     * @return a String that holds all Parents
     * @throws DatabaseException    If error in database.
     */
    public String viewAllParents() throws DatabaseException {
        StringBuilder allParents = new StringBuilder();
        // Coduri ANSI pentru culori
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RESET = "\u001B[0m";

        try {
            for (Parent p : parents.getAll()) {
                allParents.append(ANSI_RED).append("Parent").append(ANSI_RESET)
                        .append(" with id: ").append(p.getId())
                        .append(", name ").append(p.getName()).append(" ").append(p.getLastName())
                        .append(" has childrens: ");
                for (int sId : p.getChildren()) {
                    Student s = null;
    try {
        s = students.get(sId);
    } catch (DatabaseException e) {
        throw e;
    }                allParents.append("\n")
                            .append(ANSI_GREEN).append("Student").append(ANSI_RESET)
                            .append(" with id: ").append(s.getId())
                            .append(" ").append(s.getLastName()).append(" ").append(s.getName());
                }
                allParents.append("\n");
            }
        } catch (DatabaseException e) {
            throw e;
        }
        return allParents.toString();
    }


    /**
     * display all contests
     * @return a string that holds all contests
     * @throws DatabaseException    If error in database.
     */
    public String viewAllContests()  throws DatabaseException {
        StringBuilder allContests = new StringBuilder();

        // Coduri ANSI pentru culori
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_ORANGE = "\u001B[38;5;214m"; // Portocaliu
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_RESET = "\u001B[0m";

        try {
            for (Contest c : contests.getAll()) {
                allContests.append(c.toString2()).append('\n');

                for (int sId : c.getStudents()) {
                    allContests.append(students.get(sId).toString3()).append('\n');
                }

                allContests.append('\n');
            }
        } catch (DatabaseException e) {
            throw e;
        }

        return allContests.toString();
    }

    /**
     * display all Training Camps
     * @return a String that holds all Training Camps
     * @throws DatabaseException    If error in database.
     */
    public String viewTrainingCamps() throws DatabaseException {
        StringBuilder allTrainingCamps = new StringBuilder();
        try {
            for(TrainingCamp t: trainingCamps.getAll()){
                allTrainingCamps.append(t.toString2()).append('\n');
                for(int sId: t.getStudents()){
                    allTrainingCamps.append(students.get(sId).toString2()).append('\n');
                }
                allTrainingCamps.append('\n');
            }
        } catch (DatabaseException e) {
            throw e;
        }
        return allTrainingCamps.toString();
    }

    /**
     * display all BeltExams
     * @return a string that holds all BeltExams
     * @throws DatabaseException    If error in database.
     */
    public String viewBeltExams()  throws DatabaseException {
        StringBuilder allBeltExams = new StringBuilder();
        try {
            for (BeltExam b : beltExams.getAll()) {
                allBeltExams.append(b.toString2()).append('\n');
                for (int sId : b.getListOfResults().keySet()) {
                    allBeltExams.append(students.get(sId).toString2()).append('\n');
                }
                allBeltExams.append('\n');
            }
        } catch (DatabaseException e) {
            throw e;
        }
        return allBeltExams.toString();
    }


    /**
     * Sorts the contests based on their starting date.
     * @return a sorted List of Contests based on ther starting dates
     * @throws DatabaseException    If error in database.
     */
    public List<Contest> sortContestsByDates() throws DatabaseException{
        List<Contest> sorted = null;
        try {
            sorted = new ArrayList<>(contests.getAll());
        } catch (DatabaseException e) {
            throw e;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        sorted.sort((c1, c2) -> {
            LocalDate date1 = LocalDate.parse(c1.startDate, formatter);
            LocalDate date2 = LocalDate.parse(c2.startDate, formatter);
            return date1.compareTo(date2);
        });
        return sorted;
    }

    /**
     * Sorts the belt exams based on their starting date.
     * @return a sorted List of Belt Exams based on their starting dates
     * @throws DatabaseException    If error in database.
     */

    public List<BeltExam> sortBeltExamnsByDates() throws DatabaseException{
        List<BeltExam> sorted = null;
        try {
            sorted = new ArrayList<>(beltExams.getAll());
        } catch (DatabaseException e) {
            throw e;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        sorted.sort((c1, c2) -> {
            LocalDate date1 = LocalDate.parse(c1.startDate, formatter);
            LocalDate date2 = LocalDate.parse(c2.startDate, formatter);
            return date1.compareTo(date2);
        });
        return sorted;
    }

    /**
     * Sorts the training camps based on their starting dates.
     * @return a sorted list of training Camps based on their starting date
     * @throws DatabaseException    If error in database.
     */
    public List<TrainingCamp> sortTrainingCampsByDates() throws DatabaseException{
        List<TrainingCamp> sorted = null;
        try {
            sorted = new ArrayList<>(trainingCamps.getAll());
        } catch (DatabaseException e) {
            throw e;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        sorted.sort((c1, c2) -> {
            LocalDate date1 = LocalDate.parse(c1.startDate, formatter);
            LocalDate date2 = LocalDate.parse(c2.startDate, formatter);
            return date1.compareTo(date2);
        });
        return sorted;
    }

    /**
     * Sorts the sessions based on their number of participants.
     * @return a list of Session sorted based on their number of participants
     * @throws DatabaseException    If error in database.
     */
    public List<Session> sortSessionByNumberOfParticipants() throws DatabaseException{
        List<Session> sorted = null;
        try {
            sorted = new ArrayList<>(sessions.getAll());
        } catch (DatabaseException e) {
            throw e;
        }
        sorted.sort(Comparator.comparingInt(c -> c.getSessionStudents().size()));
        return sorted;
    }

    /**
     * Sorts the students based on their name.
     * @return a list of students ordered alphabetical
     * @throws DatabaseException    If error in database.
     */
    public List<Student> sortStudentsAlphabetical() throws DatabaseException{
        List<Student> sorted = null;
        try {
            sorted = new ArrayList<>(students.getAll());
        } catch (DatabaseException e) {
            throw e;
        }
        sorted.sort(Comparator.comparing(s -> s.name));
        return sorted;
    }

    /**
     * Sorts the students based on their number of attendances.
     * @return a list of Students ordered by number of Attendances
     * @throws EntityNotFoundException or DatabaseException    If no student found.
     */
    public List<Student> sortStudentsByNumberOfAttendences() throws DatabaseException, EntityNotFoundException {
        List<Student> sorted = null;
        try {
            sorted = new ArrayList<>(students.getAll());
        } catch (DatabaseException e) {
            throw e;
        }
        try{
        sorted.sort((s1, s2) -> {
            try {
                return Integer.compare(
                        numberOfAttendencesAndAbsences(s1.getId()).get("Attendences"),
                        numberOfAttendencesAndAbsences(s2.getId()).get("Attendences")
                );
            } catch (DatabaseException | EntityNotFoundException e) {
                throw new RuntimeException(e); // Directly rethrow the exception
            }
        });
    } catch (RuntimeException e) {
        // Unwrap and rethrow the original checked exceptions
        if (e.getCause() instanceof DatabaseException) {
            throw (DatabaseException) e.getCause();
        } else if (e.getCause() instanceof EntityNotFoundException) {
            throw (EntityNotFoundException) e.getCause();
        } else {
            throw e; // Rethrow as RuntimeException if cause is unknown
        }
    }
        return sorted;
    }

    /**
     * Filters the students based on a belt level
     * @param   beltLevel The belt level upon which the filter will apply
     * @return  List of all students having the belt level specified
     * @throws DatabaseException    If error in database.
     */
    public List<Student> filterStudentsByBelt(BeltLevel beltLevel) throws DatabaseException {
        List<Student> filtered = null;
        try {
            filtered = new ArrayList<>(students.getAll());
        } catch (DatabaseException e) {
            throw e;
        }
        filtered = filtered.stream().filter((s1)->s1.getBeltLevel().equals(beltLevel)).toList();
        return filtered;
    }

    /**
     * Filters parents based of their number of children
     * @param noOfChildren      number of children we want to filter
     * @return                  a list of parents that have noOfChildren as number of children
     * @throws DatabaseException    If error in database.
     */
    public List<Parent> filterParentsNumberOfChildren(int noOfChildren) throws DatabaseException{
        try {
            return parents.getAll().stream()  // Obținem stream-ul de la lista de părinți
                    .filter(p -> p.getChildren().size() == noOfChildren)  // Filtrăm părinții care au exact numărul de copii dorit
                    .collect(Collectors.toList());  // Colectăm rezultatele într-o listă
        } catch (DatabaseException e) {
            throw e;
        }
    }

    /**
     * Gets for a given session the date with the highest attendance and number of participants. It searches through each student's
     * session date list to find the most attended date for all students.
     * @param sessionId         The unique identifier of the session.
     * @return                  A simple entry containing the most attended date and the number of students.
     * @throws EntityNotFoundException or DatabaseException      If no session was found.
     * @throws BusinessLogicException       If there wasn't a profitable date found.
     */
    public AbstractMap.SimpleEntry<String, Double> getMostProfitableDateForSession(int sessionId) throws DatabaseException, EntityNotFoundException, BusinessLogicException {
        try {
            if(sessions.getAll().stream().noneMatch(ss -> ss.getId() == sessionId)){
                throw new EntityNotFoundException("Invalid session ID");
        }

        } catch (DatabaseException e) {
            throw e;
        }

        Map<String,Double> freqWeekdays = new HashMap<>();
        try {
            for(Student st: students.getAll()){
                if(st.session == sessionId){
                    for(SessionDate sd: st.getSessionDateList()) {
                        if(sd.isAttended()) {
                            if (freqWeekdays.containsKey(sd.getDate())){
                                freqWeekdays.replace(sd.getDate(),freqWeekdays.get(sd.getDate())+sessions.get(sessionId).getPricePerSession());
                            }
                            else{
                                freqWeekdays.put(sd.getDate(),sessions.get(sessionId).getPricePerSession());
                            }
                        }
                    }
                }
            }
        } catch (DatabaseException e) {
            throw e;
        }
        double max = 0;
        String date = "";
        for(String d: freqWeekdays.keySet()){
            if(freqWeekdays.get(d)>max){
                max = freqWeekdays.get(d);
                date = d;
            }
        }
        if(max == 0){
            throw new BusinessLogicException("No date is profitable");
        }
        else {
            return new AbstractMap.SimpleEntry<String, Double>(date, max);
        }
    }

    /**
     * Gets the first free student id
     * @return  First free student id
     * @throws DatabaseException    If error in database.
     */
    public int getStudentId() throws DatabaseException {
        return students.getAll().size()+1;
    }

    /**
     * Gets the first free session id
     * @return  First free session id
     * @throws DatabaseException    If error in database.
     */
    public int getSessionId() throws DatabaseException {
        return sessions.getAll().size()+1;
    }

    /**
     * Gets the first free trainer id
     * @return  First free trainer id
     * @throws DatabaseException    If error in database.
     */
    public int getTrainerId() throws DatabaseException {
        return trainers.getAll().size()+1;
    }

    /**
     * Gets the first free parent id
     * @return  First free parent id
     * @throws DatabaseException    If error in database.
     */
    public int getParentId() throws DatabaseException {
        return parents.getAll().size()+1;
    }

    /**
     * Gets the first free contest id
     * @return  First free contest id
     * @throws DatabaseException    If error in database.
     */
    public int getContestId() throws DatabaseException {
        return contests.getAll().size()+1;
    }

    /**
     * Gets the first free training camp id
     * @return  First free training camp id
     * @throws DatabaseException    If error in database.
     */
    public int getTrainingCampId() throws DatabaseException {
        return trainingCamps.getAll().size()+1;
    }

    /**
     * Gets the first free belt exam id
     * @return  First free belt exam id
     * @throws DatabaseException    If error in database.
     */
    public int getBeltExamId() throws DatabaseException {
        return beltExams.getAll().size()+1;
    }
}

