package iimscollege.edu.np.campusspaceandresourcebookingsystem;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SystemBackendModule {

    // 1. ENUMS
    public enum Role { STUDENT, STAFF, ADMIN }
    public enum ResourceType { ROOM, LAB, EQUIPMENT }
    public enum BookingStatus { PENDING, APPROVED, DISAPPROVED, CANCELLED }

    // 2. EXCEPTIONS
    public static class UnAuthorizedAccessException extends Exception {
        public UnAuthorizedAccessException(String message) { super(message); }
    }

    public static class ResourceUnavailableException extends Exception {
        public ResourceUnavailableException(String message) { super(message); }
    }

    public static class InvalidBookingDurationException extends Exception {
        public InvalidBookingDurationException(String message) { super(message); }
    }

    // 3. MODELS
    public abstract static class BaseEntity implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String id;

        public BaseEntity(String id) { this.id = id; }
        public abstract String getSummary();
        public String getId() { return id; }
    }

    public static class User extends BaseEntity {
        private final String username;
        private final String password;
        private final Role role;

        public User(String username, String password, Role role) {
            super(username);
            this.username = username;
            this.password = password;
            this.role = role;
        }

        public String getSummary() { return "User[" + getId() + "]: " + role; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public Role getRole() { return role; }
    }

    public static class Resource extends BaseEntity {
        private String name;
        private ResourceType type;
        private String location;
        private int capacity;
        private boolean available;

        public Resource(String id, String name, ResourceType type, String location, int capacity, boolean available) {
            super(id);
            this.name = name;
            this.type = type;
            this.location = location;
            this.capacity = capacity;
            this.available = available;
        }

        public String getSummary() { return "Resource[" + getId() + "]: " + name; }
        public String getName() { return name; }
        public ResourceType getType() { return type; }
        public String getLocation() { return location; }
        public int getCapacity() { return capacity; }
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
    }

    public static class Booking extends BaseEntity {
        private final String resourceId;
        private final String username;
        private final String purpose;
        private final String bookingDate;
        private BookingStatus status;

        public Booking(String id, String resourceId, String username, String purpose, String bookingDate) {
            super(id);
            this.resourceId = resourceId;
            this.username = username;
            this.purpose = purpose;
            this.bookingDate = bookingDate;
            this.status = BookingStatus.PENDING;
        }

        public String getSummary() { return "Booking[" + getId() + "]: " + status; }
        public String getResourceId() { return resourceId; }
        public String getUsername() { return username; }
        public String getPurpose() { return purpose; }
        public String getBookingDate() { return bookingDate; }
        public BookingStatus getStatus() { return status; }
        public void setStatus(BookingStatus status) { this.status = status; }
    }

    // 4. MAIN SERVICE HANDLER
    public static class SystemBackendService {
        private static final String DATA_DIR = "data";
        private static final String USERS_FILE = DATA_DIR + File.separator + "users.dat";
        private static final String RESOURCES_FILE = DATA_DIR + File.separator + "resources.dat";
        private static final String BOOKINGS_FILE = DATA_DIR + File.separator + "bookings.dat";
        private static final String LOGS_FILE = DATA_DIR + File.separator + "logs.dat";

        private Map<String, User> users = new HashMap<>();
        private Map<String, Resource> resources = new LinkedHashMap<>();
        private Map<String, Booking> bookings = new LinkedHashMap<>();
        private List<String> logs = new ArrayList<>();
        private User currentUser = null;

        // File persistence (resources.dat) is untouched; this is what getAllResources()/
        private final ResourceManager<Resource> resourceManager = new ResourceManager<>(Resource::getId);

        public SystemBackendService() {
            new File(DATA_DIR).mkdirs();
            loadAllData();
        }

        @SuppressWarnings("unchecked")
        private void loadAllData() {
            boolean usersLoaded = false;

            Object u = readFromFile(USERS_FILE);
            if (u instanceof Map) { users = (Map<String, User>) u; usersLoaded = true; }

            Object r = readFromFile(RESOURCES_FILE);
            if (r instanceof Map) resources = (Map<String, Resource>) r;

            Object b = readFromFile(BOOKINGS_FILE);
            if (b instanceof Map) bookings = (Map<String, Booking>) b;

            Object l = readFromFile(LOGS_FILE);
            if (l instanceof List) logs = (List<String>) l;

            if (!usersLoaded) {
                seedInitialData();
                saveUsers();
                saveResources();
                saveBookings();
                saveLogs();
            }

            resourceManager.reset(resources.values());
        }

        private Object readFromFile(String path) {
            File file = new File(path);
            if (!file.exists()) return null;
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                return in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                return null;
            }
        }

        private void writeToFile(String path, Object data) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
                out.writeObject(data);
            } catch (IOException e) {
                logs.add("Failed to save file: " + path);
            }
        }

        private void saveUsers() { writeToFile(USERS_FILE, users); }
        private void saveResources() { writeToFile(RESOURCES_FILE, resources); }
        private void saveBookings() { writeToFile(BOOKINGS_FILE, bookings); }
        private void saveLogs() { writeToFile(LOGS_FILE, logs); }

        private void seedInitialData() {
            users.put("admin", new User("admin", "admin123", Role.ADMIN));
            users.put("student1", new User("student1", "pass123", Role.STUDENT));

            resources.put("RM-101", new Resource("RM-101", "Study Room A", ResourceType.ROOM, "Library Level 2", 4, true));
            resources.put("LAB-B4", new Resource("LAB-B4", "Chemistry Lab", ResourceType.LAB, "Science Block A", 12, true));
            resources.put("RM-205", new Resource("RM-205", "Study Room B", ResourceType.ROOM, "Library Level 3", 6, true));
            resources.put("EVN-AUD", new Resource("EVN-AUD", "Main Auditorium", ResourceType.ROOM, "Main Hall", 200, false));
            resources.put("EQP-772", new Resource("EQP-772", "4K Projector", ResourceType.EQUIPMENT, "Tech Store", 0, true));

            log(null, "System initialized with default seed data.");
        }

        public User login(String username, String password) throws UnAuthorizedAccessException {
            User u = users.get(username);
            if (u == null || !u.getPassword().equals(password)) {
                throw new UnAuthorizedAccessException("Invalid username or password.");
            }
            this.currentUser = u;
            log(u, "User logged in");
            saveLogs();
            return currentUser;
        }

        public void logout() {
            if (currentUser != null) {
                log(currentUser, "User logged out");
                saveLogs();
                currentUser = null;
            }
        }

        public void registerUser(User user) throws Exception {
            if (users.containsKey(user.getUsername())) {
                throw new Exception("Username already exists.");
            }
            users.put(user.getUsername(), user);
            log(user, "Registered new User");
            saveUsers();
            saveLogs();
        }

        public User getCurrentUser() { return currentUser; }

        public List<Resource> getAllResources() {
            return resourceManager.getAll();
        }

        public Resource getResourceById(String id) {
            return resourceManager.get(id);
        }

        public void createBooking(String resourceId, String purpose, String date)
                throws ResourceUnavailableException, UnAuthorizedAccessException, InvalidBookingDurationException {

            if (currentUser == null) {
                throw new UnAuthorizedAccessException("Please login first.");
            }

            if (date == null || date.trim().isEmpty()) {
                throw new InvalidBookingDurationException("Booking date cannot be empty.");
            }

            try {
                java.time.LocalDate requestedDate = java.time.LocalDate.parse(date.trim());
                java.time.LocalDate today = java.time.LocalDate.now();

                if (requestedDate.isBefore(today)) {
                    throw new InvalidBookingDurationException("Cannot book a resource for a past date (" + date + ").");
                }
            } catch (java.time.format.DateTimeParseException e) {
                throw new InvalidBookingDurationException("Invalid date format. Please use YYYY-MM-DD.");
            }

            Resource res = resourceManager.get(resourceId);
            if (res == null || !res.isAvailable()) {
                throw new ResourceUnavailableException("Resource is unavailable.");
            }

            String bookingId = "BK-" + (bookings.size() + 101);
            Booking booking = new Booking(bookingId, resourceId, currentUser.getUsername(), purpose, date);
            bookings.put(bookingId, booking);

            log(booking, "Booking created");
            saveBookings();
            saveLogs();
        }

        public List<Booking> getAllBookings() {
            return new ArrayList<>(bookings.values());
        }

        public List<Booking> getUserBookings(String username) {
            List<Booking> userList = new ArrayList<>();
            for (Booking b : bookings.values()) {
                if (b.getUsername().equalsIgnoreCase(username)) userList.add(b);
            }
            return userList;
        }

        public void updateBookingStatus(String bookingId, BookingStatus status) throws Exception {
            Booking b = bookings.get(bookingId);
            if (b == null) throw new Exception("Booking not found.");

            b.setStatus(status);

            if (status == BookingStatus.APPROVED) {
                Resource r = resourceManager.get(b.getResourceId());
                if (r != null) r.setAvailable(false);
            } else if (status == BookingStatus.DISAPPROVED || status == BookingStatus.CANCELLED) {
                Resource r = resourceManager.get(b.getResourceId());
                if (r != null) r.setAvailable(true);
            }

            log(b, "Booking Status Updated");
            saveBookings();
            saveResources();
            saveLogs();
        }

        public List<String> getSystemLogs() { return new ArrayList<>(logs); }

        private void log(Object entity, String action) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            String summary = "";
            if (entity instanceof BaseEntity baseEntity) {
                summary = baseEntity.getSummary();
            }

            if (summary.isEmpty()) {
                logs.add("[" + timestamp + "] " + action);
            } else {
                logs.add("[" + timestamp + "] " + action + " -> " + summary);
            }
        }
    }
}