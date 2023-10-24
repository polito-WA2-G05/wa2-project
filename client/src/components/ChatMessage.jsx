const ChatMessage = ({ received, message, timestamp }) => {
    
    const messageStyle = {
      backgroundColor: !received ? '#007BFF' : '#f0f0f0',
      color: !received ? '#fff' : '#333',
      padding: '8px',
      margin: '8px',
      borderRadius: '18px',
      display: 'inline-block',
      width: '40%',
      float: !received ? 'right' : 'left',
    };
    
    const datetime = new Date(timestamp).toLocaleString().split(",");
    const date = datetime[0];
    const hour = datetime[1];

    return (
      <div className={`${!received ? "ms-auto" : "me-auto"}`} style={messageStyle}>
        <small style={{ color: !received  ? 'white' : 'gray' }}>{date} {hour}</small>
        <p className={"mt-2 fw-semibold"}>{message}</p>
    </div>
    );
  };
  
  export default ChatMessage;
  