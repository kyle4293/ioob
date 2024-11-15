import React from 'react';

const EditProjectModal = ({ projectName, projectDescription, onClose, onSave, setProjectName, setProjectDescription }) => {
  return (
    <div className="modal">
      <div className="modal-content">
        <h3>프로젝트 편집</h3>
        <div className="modal-row">
          <label className="modal-label">이름</label>
          <input
            type="text"
            className="modal-input"
            value={projectName}
            onChange={(e) => setProjectName(e.target.value)}
          />
        </div>
        <div className="modal-row">
          <label className="modal-label">설명</label>
          <input
            type="text"
            className="modal-input"
            value={projectDescription}
            onChange={(e) => setProjectDescription(e.target.value)}
          />
        </div>
        <div className="modal-actions">
          <button onClick={onSave}>저장</button>
          <button onClick={onClose}>취소</button>
        </div>
      </div>
    </div>
  );
};

export default EditProjectModal;
