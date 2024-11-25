package com.projeto.BackendContratanti.Reposotory;

import com.projeto.BackendContratanti.Dto.UsuarioResponseDTO;
import com.projeto.BackendContratanti.Model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, BigInteger> {

    Optional<Usuario> findOptionalByEmail(String email);

    UserDetails findByEmail(String login);

    Optional<Usuario> findByCpf(String cpf);

    @Query(value = "SELECT new com.example.dto.UsuarioResponseDTO(u.id, u.nome, u.email) FROM Usuario u")
    List<UsuarioResponseDTO> buscarComLimite(Pageable pageable);

    @Query("SELECT new com.projeto.BackendContratanti.Dto.UsuarioResponseDTO(" +
            "    u.id, u.nome, u.email, u.telefone, u.url_curriculo, u.url_linkedin, " +
            "    u.url_github, u.cpf, u.cidade, u.senha, u.descricao) " +
            "FROM Usuario u " +
            "WHERE u.email IN (" +
            "    SELECT c.email " +
            "    FROM Competencia c " +
            "    WHERE c.nome IN :competencias " +
            "    GROUP BY c.email " +
            "    HAVING COUNT(DISTINCT c.nome) = :quantidade" +
            ")")
    List<UsuarioResponseDTO> buscarUsuariosPorCompetencias(
            @Param("competencias") List<String> competencias,
            @Param("quantidade") Long quantidade);

}
