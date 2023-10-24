// Imports
import {Button, Dropdown, DropdownButton} from "react-bootstrap";
import React, {useContext} from 'react'

// Styles
import {IoIosNotifications, IoMdTrash} from 'react-icons/io'
import {NotificationContext} from '@contexts';

const NotificationCenter = () => {
    const {notifications, deleteNotification} = useContext(NotificationContext)

    return (
        <Dropdown className="ms-4">
            <DropdownButton
                variant="outline-primary"
                id="dropdown-notifications"
                align={"end"}
                title={<IoIosNotifications size={20}/>}
            >
                {notifications.length === 0 ? <Dropdown.ItemText>No notifications</Dropdown.ItemText> :
                    notifications.map((notification, idx) => (
                        <div key={notification.id}>
                            <div className={"d-flex align-items-center py-2"}>
                                <Dropdown.Item disabled>
                                    <Dropdown.ItemText className={"fw-bold"}>{notification.text}</Dropdown.ItemText>
                                    <Dropdown.ItemText
                                        className={"small"}>{new Date(notification.timestamp).toLocaleString()}</Dropdown.ItemText>
                                </Dropdown.Item>
                                <Button variant="outline-danger" className={"me-4"}
                                        onClick={() => deleteNotification(notification.id)}>
                                    <IoMdTrash size={20}/>
                                </Button>
                            </div>
                            {idx < notifications.length - 1 && <Dropdown.Divider/>}
                        </div>
                    ))}
            </DropdownButton>
        </Dropdown>
    )
}
export default NotificationCenter