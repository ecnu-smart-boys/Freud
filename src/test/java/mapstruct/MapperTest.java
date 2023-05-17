package mapstruct;

import org.ecnusmartboys.infrastructure.data.mysql.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MapperTest {

    @Test
    public void test(){
        var m = Mappers.getMapper(NestedFieldMapper.class);
        var user = new User();
        user.setId(200L);
        user.setEmail("2@2.c");
        user.setAge(2);

        var nested = m.toDto(user);
        assertNotNull(nested);
        assertNotNull(nested.getUser());
        assertEquals(nested.getUser().getEmail(), user.getEmail());
        assertEquals(nested.getUser().getAge(), user.getAge());
    }
}
