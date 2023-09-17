const UserProfile = ({user, imageNumber}) => {
    const gender = user.gender === "female" ? "women" : "men";

    return (
        <div>
            {/*<p>{name}</p>
            <p>{age}</p>
            <img src={`https://randomuser.me/api/portraits/${gender}/75.jpg`}/>
            {props.children}*/}
            <ul>
                <li>
                    <h1>{user.name}</h1>
                    <p>{user.gender}</p>
                    <p>{user.age}</p>
                    <img src={`https://randomuser.me/api/portraits/${gender}/${imageNumber}.jpg`} alt={user.name}/>
                </li>
            </ul>
        </div>
    )
}

export default UserProfile;