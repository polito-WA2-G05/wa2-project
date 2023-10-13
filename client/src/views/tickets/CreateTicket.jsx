// Imports
import { Col } from "react-bootstrap";

// Components
import { CreateTicketForm } from "../../components/forms";

// Components
const CreateTicket = () => {
	return (
		<div>
			<h1 className="fw-extrabold text-center">Create a new ticket</h1>
			<Col xs={{ span: 12 }} lg={{ span: 4 }} className="mx-auto">
				<CreateTicketForm />
			</Col>
		</div>
	);
};

export default CreateTicket;
