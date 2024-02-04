package com.re.back.entities.auth

import com.re.back.enums.UserRole
import jakarta.persistence.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
class AppUser(
    @Column(unique = true) var userName: String,
    @Column(unique = true) var email: String,
    var hashedPassword: String,
    var bio: String? = null,
    @Enumerated(EnumType.STRING) var role: UserRole = UserRole.ROLE_USER,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
) : UserDetails {
    override fun getAuthorities(): List<SimpleGrantedAuthority> {
        return listOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword(): String = hashedPassword

    override fun getUsername(): String = userName

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

}