package org.example.spring2025demo3rest.controllers;

import org.example.spring2025demo3rest.dataaccess.AutoRepository;
import org.example.spring2025demo3rest.dataaccess.HomeRepository;
import org.example.spring2025demo3rest.dataaccess.UserRepository;
import org.example.spring2025demo3rest.pojos.Auto;
import org.example.spring2025demo3rest.pojos.Home;
import org.example.spring2025demo3rest.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * The main controller for this application. Controllers can be split by the base URL in the request mapping
 */
@Controller
@RequestMapping(path = RESTNouns.VERSION_1)
public class MainController {

    //Wire the ORM
    @Autowired private UserRepository userRepository;
    @Autowired
    private HomeRepository homeRepository;
    @Autowired
    private AutoRepository autoRepository;

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

    /**
     * Put mapping for user
     * @param userId
     * @param name
     * @param email
     * @return
     */
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

    /**
     * Get all homes for a specific User
     */
    @GetMapping(path = RESTNouns.USER +  RESTNouns.ID + RESTNouns.HOME)
    public @ResponseBody Iterable<Home> getAllHomesByUser(@PathVariable("id") Long userId) {
        Iterable<Home> homes = null;
        if (userRepository.existsById(userId)) {
            Optional<User> user = userRepository.findById(userId);
            if(user.isPresent()){
               // homeRepository

                homes = homeRepository.getAllByUserId(userId);
            }
        }

        //TODO handle errors

        return homes;
    }

    /**
     * Create a home for a user
     * @param userId user id
     * @param dateBuilt date built
     * @param value value of the home as an int
     * @return
     */
    @PostMapping(path = RESTNouns.USER + RESTNouns.ID + RESTNouns.HOME)
    public @ResponseBody Home createHomeByUser(
            @PathVariable("id") Long userId,
            @RequestParam LocalDate dateBuilt,
            @RequestParam int value,
            @RequestParam Home.HeatingType heatingType,
            @RequestParam Home.Location location) {

        if (userRepository.existsById(userId)) {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                Home home = new Home();
                home.setValue(value);
                home.setDateBuilt(dateBuilt);
                home.setHeatingType(heatingType);
                home.setLocation(location);
                home.setUser(user.get());
                return homeRepository.save(home);
            }
        }

        return null;
    }

    @DeleteMapping(path = RESTNouns.USER + RESTNouns.ID + RESTNouns.HOME + RESTNouns.HOME_ID)
    public @ResponseBody String deleteHomeByUser(
            @PathVariable("id") Long userId,
            @PathVariable("homeId") Long homeId) {

        Optional<Home> homeOpt = homeRepository.findById(homeId);

        if (homeOpt.isPresent()) {
            Home home = homeOpt.get();

            // Safely check if the user exists and matches
            if (home.getUser() != null && home.getUser().getId() != null && home.getUser().getId().longValue() == userId) {
                homeRepository.deleteById(homeId);
                return "Home with ID " + homeId + " deleted successfully.";
            }
        }

        return "Home not found or does not belong to the specified user.";
    }

    @PutMapping(path = RESTNouns.USER + RESTNouns.ID + RESTNouns.HOME + RESTNouns.HOME_ID)
    public @ResponseBody String updateHomeByUser(
            @PathVariable("id") Long userId,
            @PathVariable("homeId") Long homeId,
            @RequestParam int value,
            @RequestParam LocalDate dateBuilt,
            @RequestParam Home.HeatingType heatingType,
            @RequestParam Home.Location location) {

        Optional<Home> homeOpt = homeRepository.findById(homeId);

        if (homeOpt.isPresent()) {
            Home home = homeOpt.get();

            if (home.getUser() != null && home.getUser().getId().longValue() == userId) {
                home.setValue(value);
                home.setDateBuilt(dateBuilt);
                home.setHeatingType(heatingType);
                home.setLocation(location);
                homeRepository.save(home);
                return "Home with ID " + homeId + " updated successfully.";
            }
        }

        return "Home not found or does not belong to the specified user.";
    }

    /**
     * Get all vehicles for a specific User
     */
    @GetMapping(path = RESTNouns.USER +  RESTNouns.ID + RESTNouns.AUTO)
    public @ResponseBody Iterable<Auto> getAllVehiclesByUser(@PathVariable("id") Long userId) {
        Iterable<Auto> vehicles = null;
        if (userRepository.existsById(userId)) {
            Optional<User> user = userRepository.findById(userId);
            if(user.isPresent()){
                // homeRepository

                vehicles = autoRepository.getAllByUserId(userId);
            }
        }

        return vehicles;
    }

    /**
     * Create a vehicle for a user
     * @param userId user id
     * @param make vehicle make
     * @param model vehicle model
     * @param year year made as an int
     * @param value value of the vehicle as an int
     * @param accidentCount as an int
     * @return
     */
    @PostMapping(path = RESTNouns.USER + RESTNouns.ID + RESTNouns.AUTO)
    public @ResponseBody Auto createAutoByUser(
            @PathVariable("id") Long userId,
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam int year,
            @RequestParam int value,
            @RequestParam int accidentCount) {

        if (userRepository.existsById(userId)) {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                Auto vehicle= new Auto();
                vehicle.setMake(make);
                vehicle.setModel(model);
                vehicle.setYear(year);
                vehicle.setValue(value);
                vehicle.setAccidentCount(accidentCount);
                vehicle.setUser(user.get());
                return autoRepository.save(vehicle);
            }
        }

        return null;
    }

    @DeleteMapping(path = RESTNouns.USER + RESTNouns.ID + RESTNouns.AUTO + RESTNouns.AUTO_ID)
    public @ResponseBody String deleteAutoByUser(
            @PathVariable("id") Long userId,
            @PathVariable("autoId") Long autoId) {

        Optional<Auto> autoOpt = autoRepository.findById(autoId);

        if (autoOpt.isPresent()) {
            Auto auto = autoOpt.get();

            // Safely check if the user exists and matches
            if (auto.getUser() != null && auto.getUser().getId() != null && auto.getUser().getId().longValue() == userId) {
                autoRepository.deleteById(autoId);
                return "Vehicle with ID " + autoId + " deleted successfully.";
            }
        }

        return "Vehicle not found or does not belong to the specified user.";
    }

    @PutMapping(path = RESTNouns.USER + RESTNouns.ID + RESTNouns.AUTO + RESTNouns.AUTO_ID)
    public @ResponseBody String updateVehicleByUser(
            @PathVariable("id") Long userId,
            @PathVariable("autoId") Long autoId,
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam int year,
            @RequestParam int value,
            @RequestParam int accidentCount) {

        Optional<Auto> autoOpt = autoRepository.findById(autoId);

        if (autoOpt.isPresent()) {
            Auto auto = autoOpt.get();

            if (auto.getUser() != null && auto.getUser().getId().longValue() == userId) {
                auto.setMake(make);
                auto.setModel(model);
                auto.setYear(year);
                auto.setValue(value);
                auto.setAccidentCount(accidentCount);
                autoRepository.save(auto);
                return "Vehicle with ID " + autoId + " updated successfully.";
            }
        }

        return "Vehicle not found or does not belong to the specified user.";
    }

}
