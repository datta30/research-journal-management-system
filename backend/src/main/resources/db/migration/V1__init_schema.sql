CREATE TABLE IF NOT EXISTS user_accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    organization VARCHAR(255),
    role VARCHAR(32) NOT NULL,
    enabled BIT NOT NULL DEFAULT 1,
    last_login_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS papers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(300) NOT NULL,
    abstract_text TEXT,
    keywords TEXT,
    status VARCHAR(32) NOT NULL,
    author_id BIGINT NOT NULL,
    editor_id BIGINT,
    current_version INT,
    decision_notes TEXT,
    submitted_at DATETIME,
    published_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_paper_author FOREIGN KEY (author_id) REFERENCES user_accounts (id),
    CONSTRAINT fk_paper_editor FOREIGN KEY (editor_id) REFERENCES user_accounts (id)
);

CREATE TABLE IF NOT EXISTS paper_versions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_id BIGINT NOT NULL,
    version_number INT NOT NULL,
    content LONGTEXT,
    change_log TEXT,
    file_path VARCHAR(255),
    plagiarism_score DOUBLE,
    plagiarism_summary TEXT,
    submitted_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_paper_version_paper FOREIGN KEY (paper_id) REFERENCES papers (id)
);

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_version_id BIGINT NOT NULL,
    paper_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    recommendation VARCHAR(32),
    comments TEXT,
    submitted_at DATETIME,
    due_at DATETIME,
    plagiarism_observations TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_paper_version FOREIGN KEY (paper_version_id) REFERENCES paper_versions (id),
    CONSTRAINT fk_reviews_paper FOREIGN KEY (paper_id) REFERENCES papers (id),
    CONSTRAINT fk_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES user_accounts (id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(32) NOT NULL,
    message TEXT NOT NULL,
    resource_link VARCHAR(255),
    read_flag BIT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES user_accounts (id)
);

CREATE INDEX idx_papers_status ON papers (status);
CREATE INDEX idx_papers_author ON papers (author_id);
CREATE INDEX idx_reviews_reviewer ON reviews (reviewer_id);
CREATE INDEX idx_reviews_paper ON reviews (paper_id);
