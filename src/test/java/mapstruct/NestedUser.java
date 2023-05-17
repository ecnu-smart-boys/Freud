package mapstruct;

import lombok.Data;

@Data
public class NestedUser {

    private Long id;

    private NestedField user;
}
