public class main {

    public static void main(String[] args) {

//        UserService userService = new UserService(new DynamoDAOFactory());
//        FollowService followService = new FollowService(new DynamoDAOFactory());
//        StatusService statusService = new StatusService(new DynamoDAOFactory());
//
//        LoginRequest loginRequest = new LoginRequest("@afatchimp", "password");
//        AuthenticateResponse loginResponse = userService.login(loginRequest);
//
//        AuthToken authToken = loginResponse.getAuthToken();
//        User user = new User("Cameron", "Greenwalt", "@afatchimp", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
//        Timestamp time = new Timestamp(System.currentTimeMillis());
//        PostStatusRequest postStatusRequest = new PostStatusRequest(authToken,
//                new Status("test post", user, time.toString(), new ArrayList<>(), new ArrayList<>()));
//
//        SimpleResponse postStatusResponse = statusService.postStatus(postStatusRequest);
//
//        System.out.println();



//        // Register 10000 users who follow @afatchimp (me)
//
//        UserService userService = new UserService(new DynamoDAOFactory());
//        FollowService followService = new FollowService(new DynamoDAOFactory());
//
//        int num_users = 10000;
//        int batch_size = 25;
//        int num_batches = num_users / batch_size;
//
//        for (int batch_num = 0; batch_num < num_batches; batch_num++) {
//
//            System.out.println(String.format("Batch number %d", batch_num));
//
//            List<UserDTO> batchUsers = new ArrayList<>();
//            List<String> batchFollowerAliases = new ArrayList<>();
//
//            for (int item_num = 0; item_num < batch_size; item_num++) {
//
//                String firstName = Integer.toString(batch_num),
//                        lastName = Integer.toString(item_num),
//                        alias = "@" + firstName + "-" + lastName,
//                        imageUrl = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png",
//                        password = "password";
//                byte[] hash, salt;
//                int followersCount = 0, followingCount = 1;
//
//                Pair<byte[], byte[]> hashingResult = PasswordHasher.hashPassword(password);
//                hash = hashingResult.getFirst();
//                salt = hashingResult.getSecond();
//
//                User user = new User(firstName, lastName, alias, imageUrl);
//                UserDTO userDTO = new UserDTO(user, hash, salt, followersCount, followingCount);
//
//                batchUsers.add(userDTO);
//                batchFollowerAliases.add(alias);
//            }
//
//            // Add all users to users table
//            userService.batchAdd(batchUsers);
//
//            // Have all users follow @afatchimp
//            followService.batchFollow(batchFollowerAliases, "@afatchimp");
//        }
    }
}
