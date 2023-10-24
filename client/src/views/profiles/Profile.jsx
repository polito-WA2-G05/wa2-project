// Imports
import React, {useContext} from "react";
import {useNavigate} from "react-router-dom";
import {Button, Card, Col} from "react-bootstrap";

// Components
import {InfoCard} from "@components";

// Contexts
import {SessionContext} from "@contexts";

// Utils
import {Role} from "../../utils";

const Profile = () => {
    const {session, role} = useContext(SessionContext)

    const navigate = useNavigate();

    const info = role === Role.CUSTOMER ?
        [
            {label: "Email", value: session.details.email},
            {label: "Username", value: session.details.username}
        ]
        :
        [
            {label: "Email", value: session.details.email},
            {label: "Tickets assigned to me", value: session.info.workingOn},
            {label: "Specializations", value: session.info.specializations.map(spec => spec.name).join(', ')}
        ]

    return (
        <Col xs={12} lg={8}>
            <InfoCard
                headerTitle={`${role} Profile`}
                contentTitle={`${session.info.name ?? session.details.username} ${session.info.surname ?? ""}`}
            >
                {info.map((info) => (
                    <Card.Body key={`profile-${info.label}`}>
                        <small>{info.label}</small>
                        <p><strong>{info.value}</strong></p>
                    </Card.Body>
                ))}
                {role === Role.CUSTOMER && (
                    <Button
                        variant="warning"
                        className="py-2 px-5 rounded-3 my-4 fw-semibold"
                        onClick={() => navigate("/me/edit")}
                    >
                        Edit Profile
                    </Button>
                )}
            </InfoCard>
        </Col>
    );
};

export default Profile;
