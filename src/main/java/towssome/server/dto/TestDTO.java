package towssome.server.dto;

import jakarta.validation.constraints.Size;

public record TestDTO(

        @Size(min = 20, max = 100, message = "20자 이상, 100자 이하")
        String message

) {
}
