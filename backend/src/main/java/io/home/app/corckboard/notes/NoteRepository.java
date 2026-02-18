package io.home.app.corckboard.notes;

import io.home.app.corckboard.auth.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
    List<NoteEntity> findByUserOrderByCreatedAtDesc(UserEntity user);
}
