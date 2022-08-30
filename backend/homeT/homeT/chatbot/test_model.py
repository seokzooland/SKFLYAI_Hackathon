import torch
from transformers import PreTrainedTokenizerFast, GPT2LMHeadModel

Q_TKN = "<usr>"
A_TKN = "<sys>"
BOS = '</s>'
EOS = '</s>'
MASK = '<unused0>'
SENT = '<unused1>'
PAD = '<pad>'

koGPT2_TOKENIZER = PreTrainedTokenizerFast.from_pretrained("skt/kogpt2-base-v2",
                                                           bos_token=BOS, eos_token=EOS, unk_token='<unk>',
                                                           pad_token=PAD, mask_token=MASK)
model = GPT2LMHeadModel.from_pretrained('skt/kogpt2-base-v2')
torch_model = GPT2LMHeadModel.from_pretrained('/home/szland/model.pt')
tokenizer = PreTrainedTokenizerFast.from_pretrained("skt/kogpt2-base-v2", bos_token='</s>', eos_token='</s>',
                                                    unk_token='<unk>', pad_token='<pad>', mask_token='<mask>')


def chatbot_model(text):
    with torch.no_grad():
        ans = ""
        while 1:
            input_ids = torch.LongTensor(koGPT2_TOKENIZER.encode(Q_TKN + text + SENT + A_TKN + ans)).unsqueeze(dim=0)
            pred = torch_model(input_ids)
            pred = pred.logits
            gen = koGPT2_TOKENIZER.convert_ids_to_tokens(torch.argmax(pred, dim=-1).squeeze().numpy().tolist())[-1]

            if gen == EOS:
                break
            ans += gen.replace("▁", " ")

        print("Chatbot > {}".format(ans.strip()))
        return ans

