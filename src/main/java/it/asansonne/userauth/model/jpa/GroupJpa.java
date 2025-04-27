package it.asansonne.userauth.model.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.asansonne.userauth.model.Models;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The type Group.
 */
@Builder
@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class GroupJpa implements Models {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  @ToString.Exclude
  private Integer id;

  @Column(name = "uuid", nullable = false, unique = true, columnDefinition = "UUID")
  private UUID uuid;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @Column(name = "path", nullable = false, length = 50)
  private String path;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @JsonIgnore
  @ManyToMany(mappedBy = "groups", cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  private List<UserJpa> users;
}
