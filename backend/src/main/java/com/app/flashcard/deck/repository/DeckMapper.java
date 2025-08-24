package com.app.flashcard.deck.repository;

import com.app.flashcard.deck.model.DeckPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DeckMapper {
    DeckPojo findById(@Param("id") Integer id);
    List<DeckPojo> findByUserId(@Param("userId") Integer userId);
    List<DeckPojo> findAll();
    int insert(DeckPojo deck);
    int update(DeckPojo deck);
    int deleteById(@Param("id") Integer id);
    int updateStatistics(@Param("deckId") Integer deckId, 
                        @Param("newCards") int newCards,
                        @Param("learningCards") int learningCards, 
                        @Param("dueCards") int dueCards);
}