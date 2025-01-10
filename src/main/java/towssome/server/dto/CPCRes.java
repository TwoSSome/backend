package towssome.server.dto;


import java.time.LocalDateTime;

public record CPCRes(
        Long id,
        String body,
        ProfileSimpleRes commentedMember,
        LocalDateTime createDate,
        LocalDateTime lastModifiedDate
) {
}
