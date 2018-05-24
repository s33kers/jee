ALTER TABLE PAGE DROP CONSTRAINT fk_page_survey;
ALTER TABLE PAGE ADD CONSTRAINT fk_page_survey FOREIGN KEY (survey_id) REFERENCES survey (id) ON DELETE CASCADE;

ALTER TABLE base_question DROP CONSTRAINT fk_base_question_page;
ALTER TABLE base_question ADD CONSTRAINT fk_base_question_page FOREIGN KEY (page_id) REFERENCES page (id) ON DELETE CASCADE;

ALTER TABLE choose_question DROP CONSTRAINT FK_CHOOSE_QUESTION_BQ;
ALTER TABLE choose_question ADD CONSTRAINT FK_CHOOSE_QUESTION_BQ FOREIGN KEY (id) REFERENCES base_question (id) ON DELETE CASCADE;

ALTER TABLE scale_question DROP CONSTRAINT fk_scale_question_bq;
ALTER TABLE scale_question ADD CONSTRAINT fk_scale_question_bq FOREIGN KEY (id) REFERENCES base_question (id) ON DELETE CASCADE;

ALTER TABLE text_question DROP CONSTRAINT fk_text_question_bq;
ALTER TABLE text_question ADD CONSTRAINT fk_text_question_bq FOREIGN KEY (id) REFERENCES base_question (id) ON DELETE CASCADE;

ALTER TABLE CHOOSE_QUESTION_ANSWER DROP CONSTRAINT fk_cq_answer_cq;
ALTER TABLE CHOOSE_QUESTION_ANSWER ADD CONSTRAINT fk_cq_answer_cq FOREIGN KEY (CHOOSE_QUESTION_ID) REFERENCES CHOOSE_QUESTION (id) ON DELETE CASCADE;

ALTER TABLE QUESTION_ANSWER DROP CONSTRAINT fk_qa_question;
ALTER TABLE QUESTION_ANSWER ADD CONSTRAINT fk_qa_question FOREIGN KEY (question_id) REFERENCES base_question (id) ON DELETE CASCADE;

ALTER TABLE multiple_answers DROP CONSTRAINT FK_MA_QUESTION_ANSWER;
ALTER TABLE multiple_answers ADD CONSTRAINT FK_MA_QUESTION_ANSWER FOREIGN KEY (question_answer_id) REFERENCES QUESTION_ANSWER (id) ON DELETE CASCADE;

ALTER TABLE multiple_answers DROP CONSTRAINT FK_MA_CHOOSE_QUESTION_ANSWER;
ALTER TABLE multiple_answers ADD CONSTRAINT FK_MA_CHOOSE_QUESTION_ANSWER FOREIGN KEY (choose_question_answer_id) REFERENCES CHOOSE_QUESTION_ANSWER (id) ON DELETE CASCADE;
