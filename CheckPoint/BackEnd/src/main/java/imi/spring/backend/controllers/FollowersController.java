package imi.spring.backend.controllers;

import imi.spring.backend.models.AppUser;
import imi.spring.backend.services.FollowersService;
import imi.spring.backend.services.JWTService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping(path = "/follow_list")
public class FollowersController {
    private final FollowersService followersService;
    private final JWTService jwtService;

    /* Koga sve dati korisnik prati? */
    @GetMapping("/{userId}/following")
    @ResponseBody
    public List<AppUser> getAllFollowingByUser(@PathVariable Long userId) {
        return followersService.getAllFollowingByUser(userId);
    }

    /* Ko sve prati datog korisnika? */
    @GetMapping("/{userId}/followers")
    @ResponseBody
    public List<AppUser> getAllFollowersPerUser(@PathVariable Long userId) {
        return followersService.getAllFollowersPerUser(userId);
    }

    @PostMapping("/follow_or_unfollow/{userId}")
    @ResponseBody
    public String followOrUnfollowUser(HttpServletRequest request, @PathVariable Long userId) throws ServletException {
        AppUser userFromJWT = jwtService.getAppUserFromJWT(request);
        if (userFromJWT != null)
            return followersService.followOrUnfollowUser(userFromJWT.getId(), userId);
        return "Invalid user!";
    }

    @GetMapping("/{userId}/following/count")
    @ResponseBody
    public Integer countAllFollowingByUser(@PathVariable Long userId) {
        return followersService.countAllFollowingByUser(userId);
    }

    @GetMapping("/{userId}/followers/count")
    @ResponseBody
    public Integer countAllFollowersPerUser(@PathVariable Long userId) {
        return followersService.countAllFollowersPerUser(userId);
    }

    @GetMapping("/{userId}/following/keyword/{username}")
    @ResponseBody
    public List<AppUser> getFollowingByUsername(@PathVariable Long userId, @PathVariable String username) { return followersService.getFollowingByUsername(userId, username); }

    @GetMapping("/{userId}/followers/keyword/{username}")
    @ResponseBody
    public List<AppUser> getFollowersByUsername(@PathVariable Long userId, @PathVariable String username) { return followersService.getFollowersByUsername(userId, username); }

    /* Koga sve ja kao korisnik pratim? */
    @GetMapping("/my/following")
    @ResponseBody
    public List<AppUser> getMyFollowing(HttpServletRequest request) throws ServletException {
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null)
            return followersService.getMyFollowing(user);
        return Collections.emptyList();
    }

    /* Koga sve mene prati? */
    @GetMapping("/my/followers")
    @ResponseBody
    public List<AppUser> getMyFollowers(HttpServletRequest request) throws ServletException {
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null)
            return followersService.getMyFollowers(user);
        return Collections.emptyList();
    }

    @GetMapping("/my/following/count")
    @ResponseBody
    public Integer countMyFollowing(HttpServletRequest request) throws ServletException {
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null)
            return followersService.countMyFollowing(user);
        return 0;
    }

    @GetMapping("/my/followers/count")
    @ResponseBody
    public Integer countMyFollowers(HttpServletRequest request) throws ServletException {
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null)
            return followersService.countMyFollowers(user);
        return 0;
    }

    @GetMapping("/my/following/keyword/{username}")
    @ResponseBody
    public List<AppUser> getMyFollowingByUsername(HttpServletRequest request, @PathVariable String username) throws ServletException {
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null)
            return followersService.getMyFollowingByUsername(user, username);
        return Collections.emptyList();
    }

    @GetMapping("/my/followers/keyword/{username}")
    @ResponseBody
    public List<AppUser> getMyFollowersByUsername(HttpServletRequest request, @PathVariable String username) throws ServletException {
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null)
            return followersService.getMyFollowersByUsername(user, username);
        return Collections.emptyList();
    }

}
