package imi.spring.backend.controllers;

import imi.spring.backend.models.PostLike;
import imi.spring.backend.services.PostLikeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping(path = "/post_likes")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @GetMapping("/all")
    @ResponseBody
    public List<PostLike> getAllPostLikes() { return postLikeService.getAllPostLikes(); }

    @GetMapping("/post/{postId}")
    @ResponseBody
    public List<PostLike> getAllPostLikes(@PathVariable Long postId) { return postLikeService.getAllLikesByPostId(postId); }

    @GetMapping("/number/{postId}")
    @ResponseBody
    public Integer getNumberOfLikesByPostId(@PathVariable Long postId) { return postLikeService.getNumberOfLikesByPostId(postId); }

    @PostMapping("/save/{postId}")
    @ResponseBody
    public String likeOrUnlikePostById(HttpServletRequest request, @PathVariable Long postId) throws ServletException {
        return postLikeService.likeOrUnlikePostById(request, postId);
    }
}
