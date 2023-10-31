// Imports
import {Link} from "react-router-dom";
import {Col} from "react-bootstrap";

// Components
import {SignupForm} from "@components/forms";

const Signup = () => {
    return <Col xs={12} lg={6}>
        <h1 className="fw-bold text-center my-5">Sign up</h1>
        <SignupForm/>
        <p>
            Are you already registered?{" "}
            <Link to={"/login"}>
                Login now
            </Link>
        </p>
    </Col>
};

export default Signup;
