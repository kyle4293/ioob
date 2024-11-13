import React, { useEffect, useState } from 'react';
import { taskService } from '../../services/TaskService';
import dayjs from 'dayjs';

const CommentSection = ({ projectId, boardId, taskId }) => {
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [editingComment, setEditingComment] = useState(null);
  const [editedContent, setEditedContent] = useState('');

  useEffect(() => {
    const fetchComments = async () => {
      try {
        const commentData = await taskService.getComments(projectId, boardId, taskId);
        setComments(commentData);
      } catch (error) {
        console.error('댓글 정보를 불러오는 중 오류 발생:', error);
      }
    };

    fetchComments();
  }, [projectId, boardId, taskId]);

  const handleAddComment = async () => {
    if (newComment.trim() === '') return;

    try {
      const commentData = await taskService.addComment(projectId, boardId, taskId, { content: newComment });
      setComments([...comments, commentData]);
      setNewComment('');
    } catch (error) {
      console.error('댓글 추가 중 오류 발생:', error);
    }
  };

  const handleEditComment = async (commentId) => {
    try {
      const updatedComment = await taskService.updateComment(projectId, boardId, taskId, commentId, { content: editedContent });
      setComments(comments.map(c => (c.id === commentId ? updatedComment : c)));
      setEditingComment(null);
    } catch (error) {
      alert('권한이 없습니다. 댓글을 수정할 수 없습니다.');
      console.error('댓글 수정 중 오류 발생:', error);
    }
  };

  const handleDeleteComment = async (commentId) => {
    if (!window.confirm('정말로 이 댓글을 삭제하시겠습니까?')) return;

    try {
      await taskService.deleteComment(projectId, boardId, taskId, commentId);
      setComments(comments.filter(c => c.id !== commentId));
    } catch (error) {
      alert('권한이 없습니다. 댓글을 삭제할 수 없습니다.');
      console.error('댓글 삭제 중 오류 발생:', error);
    }
  };

  const formatDate = (dateString) => {
    return dayjs(dateString).format('YYYY-MM-DD HH:mm');
  };

  return (
    <div className="comment-section">
      <h3>댓글</h3>
      <ul>
        {comments.map(comment => (
          <li key={comment.id} className="comment-item">
            {editingComment === comment.id ? (
              <div className="modal">
                <div className="modal-content">
                  <h3>댓글 수정</h3>
                  <input
                    type="text"
                    value={editedContent}
                    onChange={(e) => setEditedContent(e.target.value)}
                    className="comment-modal-input"
                  />
                  <div className="comment-modal-actions">
                    <button onClick={() => handleEditComment(comment.id)}>저장</button>
                    <button onClick={() => setEditingComment(null)}>취소</button>
                  </div>
                </div>
              </div>
            ) : (
              <>
                <div>
                  <p className="comment-content">{comment.content}</p>
                  <div className="comment-meta">
                    <span>{comment.userName}</span> / <span>{formatDate(comment.createdAt)}</span>
                    {comment.createdAt !== comment.modifiedAt && (
                      <small className="comment-edited"> (수정: {formatDate(comment.modifiedAt)})</small>
                    )}
                  </div>
                </div>
                <div className="comment-actions">
                  <button onClick={() => {
                    setEditingComment(comment.id);
                    setEditedContent(comment.content);
                  }}>수정</button>
                  <button onClick={() => handleDeleteComment(comment.id)}>삭제</button>
                </div>
              </>
            )}
          </li>
        ))}
      </ul>

      <div className="comment-input">
        <input
          type="text"
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          placeholder="댓글을 입력하세요"
        />
        <button onClick={handleAddComment}>댓글 추가</button>
      </div>
    </div>
  );
};

export default CommentSection;
