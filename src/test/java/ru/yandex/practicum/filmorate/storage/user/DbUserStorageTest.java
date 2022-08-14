package ru.yandex.practicum.filmorate.storage.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.model.User;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DbUserStorageTest {
  private final DbUserStorage dbUserStorage;

  @Test
  @Sql(value = {"add-user.after.sql"}, executionPhase = AFTER_TEST_METHOD)
  void addUser() {
    User johnny = new User(1L,
        "johnny.doe@example.com",
        "johnny.doe",
        "Johnny", LocalDate.of(2001, 1, 21),
        new HashSet<>());
    Optional<User> userOptional = dbUserStorage.addUser(johnny);

    assertThat(userOptional)
        .isPresent()
        .hasValueSatisfying(user ->
            assertThat(user)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("email", "johnny.doe@example.com")
                .hasFieldOrPropertyWithValue("login", "johnny.doe")
                .hasFieldOrPropertyWithValue("name", "Johnny")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2001, 1, 21))
        );
  }

  @Test
  @SqlGroup({
      @Sql(value = {"add-user-when-user-exists.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
      @Sql(value = {"add-user-when-user-exists.after.sql"}, executionPhase = AFTER_TEST_METHOD)
  })
  void addUserWhenUserExists() {
    User johnny = new User(1L,
        "johnny.doe@example.com",
        "johnny.doe",
        "Johnny", LocalDate.of(2001, 1, 21),
        new HashSet<>());

    assertThatThrownBy(() -> dbUserStorage.addUser(johnny))
        .isInstanceOf(DuplicateKeyException.class);
  }

  @Test
  @SqlGroup({
      @Sql(value = {"get-user-by-id.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
      @Sql(value = {"get-user-by-id.after.sql"}, executionPhase = AFTER_TEST_METHOD)
  })
  void getUserById() {
    Optional<User> userOptional = dbUserStorage.getUserById(1L);

    assertThat(userOptional)
        .isPresent()
        .hasValueSatisfying(user ->
            assertThat(user)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("email", "john.doe@example.com")
                .hasFieldOrPropertyWithValue("login", "john.doe")
                .hasFieldOrPropertyWithValue("name", "John")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2001, 1, 21))
        );
  }

  @Test
  void getUserByIdWhenIdDoesNotExist() {
    Optional<User> userOptional = dbUserStorage.getUserById(2L);

    assertThat(userOptional).isNotPresent();
  }

  @Test
  @SqlGroup({
      @Sql(value = {"get-users.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
      @Sql(value = {"get-users.after.sql"}, executionPhase = AFTER_TEST_METHOD)
  })
  void getUsers() {
    Stream<User> userStream = dbUserStorage.getUsers();

    assertThat(userStream).isNotEmpty().hasSameElementsAs(List.of(
        new User(1L,
            "john.doe@example.com",
            "john.doe",
            "John", LocalDate.of(2001, 1, 21),
            new HashSet<>()),
        new User(2L,
            "jane.doe@example.com",
            "jane.doe",
            "Jane",
            LocalDate.of(2002, 2, 22),
            new HashSet<>()),
        new User(3L,
            "bob.doe@example.com",
            "bob.doe",
            "Bob", LocalDate.of(2003, 3, 23),
            new HashSet<>()),
        new User(4L,
            "alice.doe@example.com",
            "alice.doe",
            "Alice", LocalDate.of(2004, 4, 24),
            new HashSet<>())
    ));
  }

  @Test
  void getUsersWhenUsersDoNotExist() {
    Stream<User> userStream = dbUserStorage.getUsers();

    assertThat(userStream).isEmpty();
  }

  @Test
  @SqlGroup({
      @Sql(value = {"update-user.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
      @Sql(value = {"update-user.after.sql"}, executionPhase = AFTER_TEST_METHOD)
  })
  void updateUser() {
    User johnny = new User(1L,
        "johnny.doe@example.com",
        "johnny.doe",
        "Johnny", LocalDate.of(2001, 1, 21),
        new HashSet<>());
    Optional<User> userOptional = dbUserStorage.updateUser(johnny);

    assertThat(userOptional)
        .isPresent()
        .hasValueSatisfying(user ->
            assertThat(user)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("email", "johnny.doe@example.com")
                .hasFieldOrPropertyWithValue("login", "johnny.doe")
                .hasFieldOrPropertyWithValue("name", "Johnny")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2001, 1, 21))
        );
  }

  @Test
  void updateUserWhenUserDoesNotExist() {
    User johnny = new User(1L,
        "johnny.doe@example.com",
        "johnny.doe",
        "Johnny", LocalDate.of(2001, 1, 21),
        new HashSet<>());
    Optional<User> userOptional = dbUserStorage.updateUser(johnny);

    assertThat(userOptional).isEmpty();
  }
}