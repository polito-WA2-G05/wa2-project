// Imports
import { Col } from "react-bootstrap";

// Components
import { CreateExpertForm } from "@components/forms";

const CreateExpert = () => {
	return (
		<div className="p-4 my-4 flex-fill align-items-center">
			<h1 className="fw-extrabold text-center">Create Expert</h1>
			<Col xs={{ span: 12 }} lg={{ span: 6 }} className="mx-auto">
				<CreateExpertForm />
			</Col>
		</div>
	);
};

export default CreateExpert;
