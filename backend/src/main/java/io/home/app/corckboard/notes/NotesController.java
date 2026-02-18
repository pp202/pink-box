package io.home.app.corckboard.notes;

import io.home.app.corckboard.auth.UserEntity;
import io.home.app.corckboard.auth.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notes")
public class NotesController {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NotesController(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<NoteResponse> list(Authentication authentication) {
        UserEntity user = currentUser(authentication);
        return noteRepository.findByUserOrderByCreatedAtDesc(user).stream()
            .map(note -> new NoteResponse(note.getId(), note.getTitle(), note.getBody(), note.getCreatedAt(), note.getUpdatedAt()))
            .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(Authentication authentication, @Valid @RequestBody CreateNoteRequest request) {
        UserEntity user = currentUser(authentication);
        OffsetDateTime now = OffsetDateTime.now();

        NoteEntity note = new NoteEntity();
        note.setUser(user);
        note.setTitle(request.title());
        note.setBody(request.body());
        note.setCreatedAt(now);
        note.setUpdatedAt(now);

        NoteEntity saved = noteRepository.save(note);
        return new NoteResponse(saved.getId(), saved.getTitle(), saved.getBody(), saved.getCreatedAt(), saved.getUpdatedAt());
    }

    private UserEntity currentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    public record CreateNoteRequest(@NotBlank String title, @NotBlank String body) {}

    public record NoteResponse(Long id, String title, String body, OffsetDateTime createdAt, OffsetDateTime updatedAt) {}
}
