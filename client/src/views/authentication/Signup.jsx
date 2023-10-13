// Imports
import { Link } from "react-router-dom";
import { Col } from "react-bootstrap";

// Components
import { SignupForm } from "@components/forms";

const Signup = () => {
	return (
		<div className="p-4 my-4 flex-fill align-items-center">
			<h1 className="fw-extrabold text-center">Sign up</h1>
			<Col xs={{ span: 12 }} lg={{ span: 6 }} className="mx-auto">
				<SignupForm />
				<p>
					Are you already registered?{" "}
					<Link to={"/login"}>
						Login now
					</Link>
				</p>
			</Col>
		</div>
	);
};

export default Signup;
