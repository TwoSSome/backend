package towssome.server.dto;

import towssome.server.entity.Photo;

public record VoteAttributeDTO(
    String title,
    Photo photo
) {
}
