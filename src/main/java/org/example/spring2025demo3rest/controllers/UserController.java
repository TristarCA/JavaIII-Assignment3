package org.example.spring2025demo3rest.controllers;

import org.example.spring2025demo3rest.dataaccess.UserRepository;
import org.example.spring2025demo3rest.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * The main controller for this application. Controllers can be split by the base URL in the request mapping
 */
@Controller
@RequestMapping(path = RESTNouns.VERSION_1)
public class UserController {

    //Wire the ORM
    @Autowired private UserRepository userRepository;

    /**
     * Get Mapping for all users
     */
    @GetMapping(path = RESTNouns.USER)
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get for a user
     * @param userId
     * @return
     */
    @GetMapping(path = RESTNouns.USER + RESTNouns.ID)
    public @ResponseBody Optional<User> getUser(@PathVariable("id") Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Post Mapping for a new users
     * @param name name
     * @param email email
     * @return
     */
    @PostMapping(path = RESTNouns.USER)
    public @ResponseBody User createUser(
            @RequestParam String name, @RequestParam String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    /**
     * Delete Mapping for a user by ID
     * @param userId The ID of the user to delete
     * @return A response indicating success or failure
     */
    @DeleteMapping(path = RESTNouns.USER + RESTNouns.ID)
    public @ResponseBody String deleteUser(@PathVariable("id") Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return "User with ID " + userId + " deleted successfully.";
        } else {
            return "User with ID " + userId + " not found.";
        }
    }

    @PutMapping(path = RESTNouns.USER + RESTNouns.ID)
    public @ResponseBody String updateUser(
            @PathVariable("id") Long userId, @RequestParam String name, @RequestParam String email){
        if (userRepository.existsById(userId)) {
            Optional<User> user = userRepository.findById(userId);
            if(user.isPresent()){
                user.get().setName(name);
                user.get().setEmail(email);
            }
            userRepository.save(user.get());
            return "User with ID " + userId + " updated successfully.";
        } else {
            return "User with ID " + userId + " not found.";
        }


    }

}
