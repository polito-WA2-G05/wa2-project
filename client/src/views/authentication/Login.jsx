// Imports
import { Link } from "react-router-dom";
import { Col } from "react-bootstrap";

// Components
import { LoginForm } from "@components/forms";

const Login = () => {
	return (
		<div>
			<h1 className="fw-extrabold text-center">Sign In</h1>
			<Col xs={{ span: 12 }} lg={{ span: 4 }} className="mx-auto">
				<LoginForm />
				<p>
					Don't have an account yet?{" "}
					<Link to={"/signup"}>
						Signup now
					</Link>
				</p>
			</Col>
		</div>
	);
};
export default Login;
