const fields = {
    login: [
        { id: "username", type: "text", name: "username", placeholder: "Username" },
        { id: "password", type: "password", name: "password", placeholder: "Password" },
    ],
    signup: [
        { id: "username", type: "text", name: "username", placeholder: "Username" },
        { id: "email", type: "email", name: "email", placeholder: "Email" },
        { id: "name", type: "text", name: "name", placeholder: "Name" },
        { id: "surname", type: "text", name: "surname", placeholder: "Surname" },
        { id: "password", type: "password", name: "password", placeholder: "Password" },
        { id: "confirm-password", type: "password", name: "confirmPassword", placeholder: "Confirm Password" },
    ],
    createExpert: [
        { id: "username", type: "text", name: "username", placeholder: "Username" },
        { id: "email", type: "email", name: "email", placeholder: "Email" },
        { id: "password", type: "password", name: "password", placeholder: "Password" },
        { id: "confirm-password", type: "password", name: "confirmPassword", placeholder: "Confirm Password" },
    ],
    editProfile: [
        { id: "name", type: "text", name: "name", placeholder: "Name" },
        { id: "surname", type: "text", name: "surname", placeholder: "Surname" },
    ],
    createTicket: [
        { id: "title", type: "text", name: "title", placeholder: "Title" },
        { id: "description", type: "text", name: "description", placeholder: "Description" },
        { id: "productEAN", type: "text", name: "productEAN", placeholder: "Product EAN" },
        {
            id: "specialization", 
            name: "specialization", 
            defaultOptionLabel: "Select a specialization...", 
            as: "select"
        }
    ]
};

export default fields;
