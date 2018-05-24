CREATE OR REPLACE VIEW QUESTION_STATISTICS (QUESTION_ID, ANSWER, ANSWER_COUNT) AS
  SELECT
    qa.question_id QUESTION_ID,
    qa.answer AS ANSWER,
    count(qa.answer) AS ANSWER_COUNT
  FROM question_answer qa
    LEFT OUTER JOIN survey_answer sa ON qa.survey_answer_id = sa.id
  WHERE qa.answer IS NOT NULL AND sa.completed = TRUE
  GROUP BY qa.question_id, qa.answer
  UNION
  SELECT
    qa.question_id QUESTION_ID,
    cqa.answer AS ANSWER,
    count(cqa.answer) AS ANSWER_COUNT
  FROM question_answer qa
    LEFT OUTER JOIN survey_answer sa ON qa.survey_answer_id = sa.id
    LEFT OUTER JOIN multiple_answers ma ON qa.id = ma.question_answer_id
    LEFT OUTER JOIN choose_question_answer cqa ON ma.choose_question_answer_id = cqa.id
  WHERE qa.answer IS NULL AND sa.completed = TRUE
  GROUP BY qa.question_id, cqa.answer;

CREATE OR REPLACE VIEW SURVEY_STATISTICS (SURVEY_ID, QUESTION_TYPE, QUESTION, QUESTION_ID, USER_ID, PUBLIC_RESULTS, ANSWER_COUNT) AS
  SELECT
    s.id AS SURVEY_ID,
    bq.question_type AS QUESTION_TYPE,
    bq.question AS QUESTION,
    bq.id AS QUESTION_ID,
    s.user_id AS USER_ID,
    s.public_results AS PUBLIC_RESULTS,
    COUNT(sa.id) AS ANSWER_COUNT
  FROM survey s
    LEFT OUTER JOIN page p ON s.id = p.survey_id
    LEFT OUTER JOIN base_question bq ON p.id = bq.page_id
    LEFT OUTER JOIN survey_answer sa ON s.id = sa.survey_id
  WHERE sa.completed = TRUE
  GROUP BY bq.id, p.page_number, bq.question_number, s.id, bq.question_type, bq.question, s.user_id, s.public_results
  ORDER BY bq.id, p.page_number, bq.question_number;