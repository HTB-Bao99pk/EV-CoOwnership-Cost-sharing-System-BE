package fu.swp.evcs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fu.swp.evcs.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByJoinStatus(String status);

    List<Member> findByGroup_Id(Long groupId);

    List<Member> findByUser_Id(Long userId);

    Optional<Member> findByGroup_IdAndUser_Id(Long groupId, Long userId);
}
