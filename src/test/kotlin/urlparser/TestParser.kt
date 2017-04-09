package urlparser

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import urlparser.Entity.*
import urlparser.UserEntity.Friend
import java.util.*
import kotlin.reflect.KClass

data class Nullable(val x: Int, val y: Int?)

sealed class Entity {
    data class User(val id: Int, val userEntity: UserEntity) : Entity()
    data class Spaceship(val id: Int) : Entity()
    data class Square(val x: Int, val y: Int, val z: Int = 42) : Entity()
    class Root : Entity()
}

data class Project(val id: Int) : UserEntity()

sealed class UserEntity {
    class Root : UserEntity()
    data class Friend(val id: Int) : UserEntity()
    class Settings : UserEntity()
}

class TestParser {

    // TODO choose one solution
    // val contextProvider = KClass<*>::meAndNested
    val contextProvider = ClasspathScanner("urlparser")::getContext

    @Test
    fun testBuildRoot() {
        val root: Optional<Root> = parse("/", contextProvider)
        assertThat(root).isPresent
    }

    @Test
    fun testBuildNestedRoot() {
        val user: Optional<User> = parse("/users/152", contextProvider)
        assertThat(user.get().id).isEqualTo(152)
        assertThat(user.get().userEntity).isExactlyInstanceOf(UserEntity.Root::class.java)
    }

    @Test
    fun testBuildNestedRootWithSlash() {
        val user: Optional<User> = parse("/users/152/", contextProvider)
        assertThat(user.get().id).isEqualTo(152)
        assertThat(user.get().userEntity).isExactlyInstanceOf(UserEntity.Root::class.java)
    }


    @Test
    fun testNoMatch() {
        val spaceship: Optional<Spaceship> = parse("/spaces/25", contextProvider)
        assertThat(spaceship).isNotPresent
    }

    @Test
    fun testBuildSingleSimpleValue() {
        val spaceship: Optional<Spaceship> = parse("/spaceships/25", contextProvider)
        assertThat(spaceship.get().id).isEqualTo(25)
    }

    @Test
    fun testBuildWithMultipleSimpleValues() {
        val user: Optional<Square> = parse("/squares/152/22/44", contextProvider)
        assertThat(user.get().x).isEqualTo(152)
        assertThat(user.get().y).isEqualTo(22)
        assertThat(user.get().z).isEqualTo(44)
    }

    /**
     * No value provided for 'z'
     * 'z' have a default value, use it
     */
    @Test
    fun testBuildUsingDefaultValue() {
        val user: Optional<Square> = parse("/squares/152/22", contextProvider)
        assertThat(user.get().x).isEqualTo(152)
        assertThat(user.get().y).isEqualTo(22)
        assertThat(user.get().z).isEqualTo(42)
    }

    /**
     * No value provided for 'y' and 'y' has no default value.
     * 'y' is also nullable so set it to null
     */
    @Test
    fun testBuildUsingNullValue() {
        val user: Optional<Nullable> = parse("/nullables/42", contextProvider)
        assertThat(user.get().x).isEqualTo(42)
        assertThat(user.get().y).isNull()
    }


    @Test
    fun testBuildNestedObject() {
        val user: Optional<User> = parse("/users/152/friends/22", contextProvider)
        assertThat(user.get().id).isEqualTo(152)
        assertThat(user.get().userEntity).isExactlyInstanceOf(Friend::class.java)
        val friend = user.get().userEntity as Friend
        assertThat(friend.id).isEqualTo(22)
    }


}