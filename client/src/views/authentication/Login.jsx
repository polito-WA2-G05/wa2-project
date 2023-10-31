// Imports
import {Link} from "react-router-dom";
import {Col} from "react-bootstrap";

// Components
import {LoginForm} from "@components/forms";

const Login = () => {
    return <Col xs={12} lg={4}>
        <h1 className="fw-bold text-center my-5">Login</h1>
        <LoginForm/>
        <p>
            Don't have an account yet?{" "}
            <Link to={"/signup"}>
                Signup now
            </Link>
        </p>
    </Col>
};
export default Login;
