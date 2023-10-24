import { Rating } from "@utils"

const ratings = [
    { name: "EXCELLENT", id: Rating.EXCELLENT },
    { name: "GOOD", id: Rating.GOOD },
    { name: "FAIR", id: Rating.FAIR },
    { name: "BAD", id: Rating.BAD },
    { name: "UNACCEPTABLE", id: Rating.UNACCEPTABLE }
]

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
        { id: "description", as: "textarea", name: "description", placeholder: "Description", rows: 5 },
        {
            id: "specialization",
            name: "specialization",
            defaultOptionLabel: "Select a specialization...",
            as: "select"
        }
    ],
    surveyModal: [
        {
            id: "serviceValuation",
            name: "serviceValuation",
            defaultOptionLabel: "Rate the service",
            as: "select",
            options: ratings
        },
        {
            id: "professionality",
            name: "professionality",
            defaultOptionLabel: "Rate professionality",
            as: "select",
            options: ratings
        },
        { id: "comment", as: "textarea", name: "comment", placeholder: "Comment", rows: 4 },

    ]
};

export default fields;
