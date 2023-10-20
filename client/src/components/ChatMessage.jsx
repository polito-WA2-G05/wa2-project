const ChatMessage = ({ received, message, ts }) => {
    
    const messageStyle = {
      backgroundColor: received === true ? '#007BFF' : '#f0f0f0',
      color: received === true ? '#fff' : '#333',
      padding: '8px',
      margin: '8px',
      borderRadius: '18px',
      display: 'inline-block',
      width: '40%',
      float: received === true ? 'right' : 'left',
    };
    //ts 
    const datetime=new Date().toLocaleString().split(",");
    const date= datetime[0];
    const hour= datetime[1];

    return (
      <div className={`${received && "ms-auto"}`} style={messageStyle}>
        <p style={{ color: !received  ? 'gray' : 'black' }}>{date} {hour}</p>
        <p>{message}</p>
    </div>
    );
  };
  
  export default ChatMessage;
  