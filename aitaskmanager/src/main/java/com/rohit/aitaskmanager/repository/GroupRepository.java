package com.rohit.aitaskmanager.repository;


import com.rohit.aitaskmanager.models.Group;
import com.rohit.aitaskmanager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface GroupRepository extends JpaRepository<Group, Long> {

}
