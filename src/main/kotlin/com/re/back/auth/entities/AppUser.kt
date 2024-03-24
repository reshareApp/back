package com.re.back.auth.entities

import com.re.back.auth.enums.UserRole
import com.re.back.gems.entities.Gem
import com.re.back.gems.entities.UserTag
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
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

        @OneToMany(cascade = [CascadeType.REMOVE], mappedBy = "user")
        @OnDelete(action = OnDeleteAction.CASCADE)
        val gems : List<Gem>? = null,

        @OneToMany(cascade = [CascadeType.REMOVE], mappedBy = "user")
        @OnDelete(action = OnDeleteAction.CASCADE)
        val tags : List<UserTag>? = null,

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