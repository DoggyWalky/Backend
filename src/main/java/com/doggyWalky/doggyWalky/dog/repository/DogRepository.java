package com.doggyWalky.doggyWalky.dog.repository;

import com.doggyWalky.doggyWalky.dog.entity.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DogRepository extends JpaRepository<Dog, Long> {

}
