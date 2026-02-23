package com.architecture.layered.presentation.mvc.command;

import com.architecture.layered.application.api.CommandUseCase;
import com.architecture.layered.presentation.common.dto.Mapper;
import com.architecture.layered.presentation.common.dto.Request;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mvc/users")
public class MvcCommandController {

    private final CommandUseCase useCase;

    public MvcCommandController(CommandUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public String create(
            @ModelAttribute Request request,
            RedirectAttributes redirectAttributes
    ) {
        String id = useCase.createUser(Mapper.toDomain(request));
        redirectAttributes.addFlashAttribute("message", "User information saved successfully!");
        return "redirect:/mvc/users/search/id?id=" + id;
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable String id,
            @ModelAttribute Request request,
            RedirectAttributes redirectAttributes
    ) {
        useCase.updateUser(id, Mapper.toDomain(request));
        redirectAttributes.addFlashAttribute(
                "message", "User #" + id + " successfully updated"
        );
        return "redirect:/mvc/users/search/id?id=" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, RedirectAttributes redirectAttributes) {
        useCase.deleteUser(id);
        redirectAttributes.addFlashAttribute("message", "User deleted!");
        return "redirect:/mvc/users";
    }

}
