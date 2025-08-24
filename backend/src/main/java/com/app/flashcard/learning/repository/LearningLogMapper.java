package com.app.flashcard.learning.repository;

import com.app.flashcard.learning.model.LearningLogPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LearningLogMapper {
    LearningLogPojo findById(@Param("id") Integer id);
    List<LearningLogPojo> findByUserId(@Param("userId") Integer userId);
    List<LearningLogPojo> findByDeckId(@Param("deckId") Integer deckId);
    int insert(LearningLogPojo log);
    int deleteById(@Param("id") Integer id);
    List<LearningLogPojo> findByUserAndDate(@Param("userId") Integer userId, @Param("date") LocalDate date);
}