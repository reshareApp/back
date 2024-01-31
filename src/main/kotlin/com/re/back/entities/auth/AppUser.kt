package com.re.back.entities.auth

import com.re.back.enums.UserRole
import jakarta.persistence.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
class AppUser(
    var userName: String,
    var email: String,
    var password: String,
    var bio: String? = null,
    var role: UserRole = UserRole.USER,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
) : UserDetails {
    override fun getAuthorities(): kotlin.collections.List<SimpleGrantedAuthority> {
        return listOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword(): String = password

    override fun getUsername(): String = userName

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

}