// Imports
import React from "react";
import { useNavigate } from "react-router-dom";
import { Card, Button } from "react-bootstrap";

// Components
import { InfoCard } from "@components";

// Hooks
import { useSessionStorage } from "@hooks";

// Utils
import { Role } from "../../utils";

const Profile = () => {
	const { session } = useSessionStorage();

	const navigate = useNavigate();

	const getInfos = () => {
		return session.details.authorities[0] === Role.CUSTOMER ?
			[
				{ label: "Email", value: session.details.email },
				{ label: "Username", value: session.details.username }
			]
			:
			[
				{ label: "Email", value: session.details.email },
				{ label: "Tickets assigned to me", value: session.info.workingOn },
				{ label: "Specializations", value: session.info.specializations.map(spec => spec.name).join(', ') }
			]
	}

	return (
		<InfoCard
			headerTitle={`${session.details.authorities[0]} Profile`}
			contentTitle={`${session.info.name ?? session.details.username} ${session.info.surname ?? ""}`}
		>
			{getInfos().map((info) => (
				<Card.Text key={`profile-${info.label}`}>
					<strong>{info.label}</strong>
					<p>{info.value}</p>
				</Card.Text>
			))}
			{session.details.authorities[0] === Role.CUSTOMER && (
				<Button
					className="mt-4"
					onClick={() => navigate("/me/edit", { replace: true })}
					variant="warning"
				>
					Edit Profile
				</Button>
			)}
		</InfoCard>
	);
};

export default Profile;
