SELECT c.movement_id, c.category_id, c.amount FROM concept c INNER JOIN movement m ON c.movement_id = m.id INNER JOIN account a ON m.account_id = a.id WHERE a.user_id= 'b32587ed-a5bb-4d04-8238-1b0d6687f901';



SELECT SUM(c.amount)
        FROM concept c 
        INNER JOIN  movement m ON c.movement_id = m.id 
        INNER JOIN account a ON m.account_id = a.id
        WHERE c.category_id='00000000-0000-0000-0000-00000000004e'
        AND m.type='CHARGE'
        AND custom_date>='2018/08/01 00:00:00'
        AND custom_date<='2018/08/15 00:00:00'
        AND a.user_id = 'b32587ed-a5bb-4d04-8238-1b0d6687f901'
        AND m.date_deleted IS NULL