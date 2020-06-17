Vue.component('sendMessage', {
	data: function() {
		return {
			runningAgents: null,
			runningAgentsCb1: null,
			runningAgentsCb2: null,
			runningAgentsCb3: null,
			performatives: null,
			selectedSender: undefined,
			selectedReciever: undefined,
			selectedPerformative: undefined,
			selectedReplyTo: undefined,
			selectedContent: "",
			selectedUserArgs: "",
			selectedLanguage: "",
			selectedEncoding: "",
			selectedOntology: "",
			selectedProtocol: "",
			selectedConversationID: "",
			selectedReplyWith: "",
			selectedInReplyTo: "",
			selectedReplyBy: ""
		}
	},
	template:
		`
		<div id="master-wrap">
			<div class="container">
				<div class="content-wrap loggedUsersPanel">
					<h1>Running Agents</h1>
					<div id="loggedInUsers">
						<div v-for="ra in runningAgents">
							<div class="users-logged"><label class="username">{{ra.agentType.name}}-{{ra.agentCenter.alias}}@{{ra.agentCenter.address}}</label></div>
						</div>
					</div>
				</div>
				<div class="content-wrap sendMessage">
					<div class="message-form">
						<h1>Send ACL Message</h1>
						<form id="sendMessageForm" class="box" method="post" v-on:submit.prevent="sendMessage">
							<div>
								<label style="color:#9a9da0;">Performative:</label>
								<select placeholder="Performative" v-model="selectedPerformative">
									<option v-for="p in performatives">{{p}}</option>
								</select>
							</div>
							<div>
								<label style="color:#9a9da0;">Sender Agent:</label>
								<select v-model="selectedSender">
									<option v-for="agentr in runningAgentsCb1">{{agentr.agentType.name}}-{{agentr.agentCenter.alias}}@{{agentr.agentCenter.address}}</option>
								</select>
							</div>
							<div>
								<label style="color:#9a9da0;">Reciever Agent:</label>
								<select v-model="selectedReciever">
									<option v-for="agentrr in runningAgentsCb2">{{agentrr.agentType.name}}-{{agentrr.agentCenter.alias}}@{{agentrr.agentCenter.address}}</option>
								</select>
							</div>
							<button type="button" class="btn btn-success" v-on:click="toggle">Message Details</button>
							<div id="messageDetails" class="collapse">
									<div>
										<label style="color:#9a9da0;">Reply To Agent:</label>
										<select v-model="selectedReplyTo">
											<option v-for="agentrrr in runningAgentsCb3">{{agentrrr.agentType.name}}-{{agentrrr.agentCenter.alias}}@{{agentrrr.agentCenter.address}}</option>
										</select>
									</div>
									<div>
										<label style="color:#9a9da0;">Content:</label>
										<input type="text" placeholder="Content" v-model="selectedContent">
									</div>
									<div>
										<label style="color:#9a9da0;">User Arguments:</label>
										<input type="text" placeholder="User Arguments" v-model="selectedUserArgs">
									</div>
									<div>
										<label style="color:#9a9da0;">Language:</label>
										<input type="text" placeholder="Language" v-model="selectedLanguage">
									</div>
									<div>
										<label style="color:#9a9da0;">Encoding:</label>
										<input type="text" placeholder="Encoding" v-model="selectedEncoding">
									</div>
									<div>
										<label style="color:#9a9da0;">Ontology:</label>
										<input type="text" placeholder="Ontology" v-model="selectedOntology">
									</div>
									<div>
										<label style="color:#9a9da0;">Protocol:</label>
										<input type="text" placeholder="Protocol" v-model="selectedProtocol">
									</div>
									<div>
										<label style="color:#9a9da0;">Conversation ID:</label>
										<input type="text" placeholder="Conversation ID" v-model="selectedConversationID">
									</div>
									<div>
										<label style="color:#9a9da0;">Reply With:</label>
										<input type="text" placeholder="Reply With" v-model="selectedReplyWith">
									</div>
									<div>
										<label style="color:#9a9da0;">In Reply To:</label>
										<input type="text" placeholder="In Reply To" v-model="selectedInReplyTo">
									</div>
									<div>
										<label style="color:#9a9da0;">Reply By:</label>
										<input type="number" min="0" placeholder="Reply By" v-model="selectedReplyBy">
									</div>
							</div>
							<input class="signup-btn" type="submit" value="SendMessage">
						</form>
					</div>
				</div>
			</div>
		</div>
		`,
	mounted: function() {
		var a = this;
		
		axios.get('rest/managers/agents/running')
		.then(function(response) {
			a.runningAgents = response.data;
			a.runningAgentsCb1 = response.data;
			a.runningAgentsCb2 = response.data;
			a.runningAgentsCb3 = response.data;
		})
		.catch(function(error) {
			alert(error.response.data);
		});

		axios.get('rest/managers/messages')
			.then(function(response) {
				a.performatives = response.data;
			})
			.catch(function(error) {
				alert(error.response.data);
			});
		
	},
	methods: {
		toggle: function() {
			let x = document.getElementById("messageDetails");
			if (x.style.display === "none") {
				x.style.display = "block"
			} else {
				x.style.display = "none"
			}
			
		},
		sendMessage: function() {
			var a = this;
			
			if (a.selectedSender !== undefined && a.selectedReciever !== undefined &&
					a.selectedPerformative != undefined) {
				
				let sender;
				let reciever;
				let temp = a.runningAgentsCb1;
				let splitted = a.selectedSender.split('@')[0].split('-');
				
				for (let i = 0; i < temp.length; i++) {
					if (splitted[0] === temp[i].agentType.name && splitted[1] === temp[i].agentCenter.alias) {
						sender = temp[i];
						break;
					}
				}
				
				splitted = a.selectedReciever.split('@')[0].split('-');
				for (let i = 0; i < temp.length; i++) {
					if (splitted[0] === temp[i].agentType.name && splitted[1] === temp[i].agentCenter.alias) {
						reciever = temp[i];
						break;
					}
				}
				
				
				let aclMessage = {
					"performative": a.selectedPerformative,
					"sender": sender,
					"recievers": [reciever],
					"replyTo": null,
					"content": a.selectedContent,
					"contentObj": {},
					"userArgs": null,
					"language": a.selectedLanguage,
					"encoding": a.selectedEncoding,
					"ontology": a.selectedOntology,
					"protocol":	a.selectedProtocol,
					"conversationId": a.selectedConversationID,
					"replyWith": a.selectedReplyWith,
					"inReplyTo": a.selectedInReplyTo,
					"replyBy": a.selectedReplyBy
				}
				
				console.log("ACL Message Sent");
				
				axios.post('rest/managers/messages', aclMessage)
					.then(function(response) {
						a.selectedSender = undefined;
						a.selectedReciever = undefined;
						a.selectedPerformative = undefined;
						a.selectedReplyTo = undefined;
						a.selectedContent = "";
						a.selectedUserArgs = "";
						a.selectedLanguage = "";
						a.selectedEncoding = "";
						a.selectedOntology = "";
						a.selectedProtocol = "";
						a.selectedConversationID = "";
						a.selectedReplyWith = "";
						a.selectedInReplyTo = "";
						a.selectedReplyBy = "";
					})
					.catch(function(error) {
						alert(error.response.data);
					});
				
				document.getElementById("sendMessageForm").reset();
			}
		}
	}
});