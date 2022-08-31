import os
import torch
from transformers import PreTrainedTokenizerFast, GPT2LMHeadModel

os.chdir(os.path.dirname(os.path.abspath(__file__)))

Q_TKN = "<usr>"
A_TKN = "<sys>"
BOS = '</s>'
EOS = '</s>'
MASK = '<unused0>'
SENT = '<unused1>'
PAD = '<pad>'


class Chatbot:

    def __init__(self) -> None:
        self.koGPT2_TOKENIZER = PreTrainedTokenizerFast.from_pretrained("skt/kogpt2-base-v2",
                                                                        bos_token=BOS, eos_token=EOS, unk_token='<unk>',
                                                                        pad_token=PAD, mask_token=MASK)
        self.torch_model5 = GPT2LMHeadModel.from_pretrained('/home/szland/model.pt')

    def answer(self, question):
        with torch.no_grad():
            input_ids = self.koGPT2_TOKENIZER.encode(Q_TKN + question + SENT + A_TKN)

            gen_ids = self.torch_model5.generate(torch.tensor([input_ids]),
                                                 max_length=128,
                                                 repetition_penalty=1.0,
                                                 top_p=0.9,
                                                 do_sample=True)

            a_start_idx = torch.where(gen_ids[0] == torch.tensor(4))[0].item() + 1  # 4: <sys>
            answer = self.koGPT2_TOKENIZER.decode(gen_ids[0, a_start_idx: -1].tolist())

        return answer