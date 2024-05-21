package towssome.server.dto;

import towssome.server.enumrated.MatingStatus;

public record MatingRes(
        Long mateId,
        MatingStatus status
) {
}
