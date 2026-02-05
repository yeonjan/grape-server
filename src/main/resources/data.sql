-- 사용자당 IN_PROGRESS 상태의 포도는 하나만 허용
CREATE UNIQUE INDEX IF NOT EXISTS idx_grape_user_in_progress
    ON grapes (user_id)
    WHERE status = 'IN_PROGRESS';
