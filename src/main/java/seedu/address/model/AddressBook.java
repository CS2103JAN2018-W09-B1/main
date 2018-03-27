package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import seedu.address.model.job.Date;
import seedu.address.model.job.DateRange;
import seedu.address.model.job.Job;
import seedu.address.model.job.JobList;
import seedu.address.model.job.JobNumber;
import seedu.address.model.job.Status;
import seedu.address.model.job.VehicleNumber;
import seedu.address.model.person.Customer;
import seedu.address.model.person.Employee;
import seedu.address.model.person.UniqueEmployeeList;
import seedu.address.model.person.exceptions.DuplicateEmployeeException;
import seedu.address.model.person.exceptions.EmployeeNotFoundException;
import seedu.address.model.remark.RemarkList;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .equals comparison)
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniqueEmployeeList employees;
    private final UniqueTagList tags;
    private final JobList jobs;
    private final JobList archiveJobs;

    /*
     * The 'unusual' code block below is an non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        employees = new UniqueEmployeeList();
        tags = new UniqueTagList();
        jobs = new JobList();
        archiveJobs = new JobList();
    }

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons, Jobs and Tags in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        // For initial testing, a random job will be created for each employee
        resetData(toBeCopied);
        //createRandomJobForEachEmployee();
    }

    //// list overwrite operations

    public void setEmployees(List<Employee> employees) throws DuplicateEmployeeException {
        this.employees.setEmployees(employees);
    }

    public void setJobs(List<Job> jobs) {
        this.jobs.setJobs(jobs);
    }

    public void setTags(Set<Tag> tags) {
        this.tags.setTags(tags);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);
        setTags(new HashSet<>(newData.getTagList()));
        List<Employee> syncedEmployeeList = newData.getEmployeeList().stream()
                .map(this::syncWithMasterTagList)
                .collect(Collectors.toList());
        List<Job> syncedJobList = newData.getJobList();
        setJobs(syncedJobList);

        try {
            setEmployees(syncedEmployeeList);
        } catch (DuplicateEmployeeException e) {
            throw new AssertionError("AddressBooks should not have duplicate employees");
        }
    }

    //// job-level operations

    //@@author whenzei
    /**
     * Adds a job to CarviciM.
     */
    public void addJob(Job job) {
        jobs.add(job);
    }

    //@@author richardson0694
    /**
     * Archives job entries in CarviciM.
     */
    public void archiveJob(DateRange dateRange) {
        Iterator<Job> iterator = jobs.iterator();
        while (iterator.hasNext()) {
            Job job = iterator.next();
            Date date = job.getDate();
            Date startDate = dateRange.getStartDate();
            Date endDate = dateRange.getEndDate();
            if (dateRange.compareTo(date, startDate) >= 0 && dateRange.compareTo(date, endDate) <= 0) {
                archiveJobs.add(job);
            }
        }
    }

    //// employee-level operations

    /**
     * Adds a employee to the address book.
     * Also checks the new employee's tags and updates {@link #tags} with any new tags found,
     * and updates the Tag objects in the employee to point to those in {@link #tags}.
     *
     * @throws DuplicateEmployeeException if an equivalent employee already exists.
     */
    public void addEmployee(Employee p) throws DuplicateEmployeeException {
        Employee employee = syncWithMasterTagList(p);
        // TODO: the tags master list will be updated even though the below line fails.
        // This can cause the tags master list to have additional tags that are not tagged to any employee
        // in the employee list.
        employees.add(employee);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code AddressBook}'s tag list will be updated with the tags of {@code editedPerson}.
     *
     * @throws DuplicateEmployeeException if updating the employee's details causes the employee to be equivalent to
     *      another existing person in the list.
     * @throws EmployeeNotFoundException if {@code target} could not be found in the list.
     *
     * @see #syncWithMasterTagList(Employee)
     */
    public void updateEmployee(Employee target, Employee editedEmployee)
            throws DuplicateEmployeeException, EmployeeNotFoundException {
        requireNonNull(editedEmployee);

        Employee syncedEditedEmployee = syncWithMasterTagList(editedEmployee);
        // TODO: the tags master list will be updated even though the below line fails.
        // This can cause the tags master list to have additional tags that are not tagged to any employee
        // in the employee list.
        employees.setEmployee(target, syncedEditedEmployee);
    }

    /**
     *  Updates the master tag list to include tags in {@code employee} that are not in the list.
     *  @return a copy of this {@code employee} such that every tag in this employee points
     *  to a Tag object in the master list.
     */
    private Employee syncWithMasterTagList(Employee employee) {
        final UniqueTagList employeeTags = new UniqueTagList(employee.getTags());
        tags.mergeFrom(employeeTags);

        // Create map with values = tag object references in the master list
        // used for checking employee tag references
        final Map<Tag, Tag> masterTagObjects = new HashMap<>();
        tags.forEach(tag -> masterTagObjects.put(tag, tag));

        // Rebuild the list of employee tags to point to the relevant tags in the master tag list.
        final Set<Tag> correctTagReferences = new HashSet<>();
        employeeTags.forEach(tag -> correctTagReferences.add(masterTagObjects.get(tag)));
        return new Employee(employee.getName(), employee.getPhone(), employee.getEmail(),
                correctTagReferences);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * @throws EmployeeNotFoundException if the {@code key} is not in this {@code AddressBook}.
     */
    public boolean removeEmployee(Employee key) throws EmployeeNotFoundException {
        if (employees.remove(key)) {
            return true;
        } else {
            throw new EmployeeNotFoundException();
        }
    }

    //// tag-level operations

    public void addTag(Tag t) throws UniqueTagList.DuplicateTagException {
        tags.add(t);
    }

    //@@author richardson0694
    /**
     * Sort all employees' name in list alphabetically.
     */
    public UniqueEmployeeList sortList() {
        employees.sortName(new Comparator<Employee>() {
            @Override
            public int compare(Employee employee1, Employee employee2) {
                return employee1.getName().toString().compareToIgnoreCase(employee2.getName().toString());
            }
        });
        return employees;
    }

    //@@author yuhongherald
    /**
     * Generates a random job for each employee
     */
    private void createRandomJobForEachEmployee() {
        Job newJob;
        for (Employee employee : employees) {
            Customer customer = Customer.generateCustomer();
            VehicleNumber vehicleNumber = new VehicleNumber("SXX0000X");
            JobNumber jobNumber = new JobNumber();
            Date date = new Date();
            UniqueEmployeeList assignedEmployees = new UniqueEmployeeList();
            try {
                assignedEmployees.add(employee);
            } catch (DuplicateEmployeeException e) {
                // we just ignore
            }
            Status status = new Status("pending");
            RemarkList remarks = new RemarkList();
            newJob = new Job(customer, vehicleNumber, jobNumber, date, assignedEmployees, status, remarks);
            jobs.add(newJob);
        }
    }

    //// util methods
    //@@author
    @Override
    public String toString() {
        return employees.asObservableList().size() + " employees, " + tags.asObservableList().size() +  " tags";
        // TODO: refine later
    }

    @Override
    public ObservableList<Employee> getEmployeeList() {
        return employees.asObservableList();
    }

    @Override
    public ObservableList<Job> getJobList() {
        return jobs.asObservableList();
    }

    @Override
    public ObservableList<Tag> getTagList() {
        return tags.asObservableList();
    }

    @Override
    public ObservableList<Job> getArchiveJobList() {
        return archiveJobs.asObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddressBook // instanceof handles nulls
                && this.employees.equals(((AddressBook) other).employees)
                && this.tags.equalsOrderInsensitive(((AddressBook) other).tags));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(employees, tags);
    }
}
